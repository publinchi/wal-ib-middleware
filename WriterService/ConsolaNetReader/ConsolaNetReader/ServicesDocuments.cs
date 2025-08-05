using log4net;
using log4net.Config;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;

namespace ConsolaNetReader
{
    public class ServicesDocuments
    {
        private static readonly ILog log = LogManager.GetLogger(typeof(ServicesDocuments));


        public Microsoft.Office.Interop.Word.Document plantillaOrigen = null;

        private BackgroundWorker worker;
        public static string deposito;
        public static string temporales;
        public static string plantillas;
        public static string generated;
        public static string plantilla;
        public static string plantillaGenerals;
        public static string getDataApi;
        public static string procesingApi;
        public static string confirmationApi;
        public static string rutaOriginal;
        public const string pdf = ".pdf";
        public const string doc = ".docx";
        public PlantillaManager manager;
        public PlantillaGeneralManager general;


        private static byte[] _contenido;

        public static void CargarDesdeArchivo(string rutaArchivo)
        {
            if (_contenido == null)
            {
                _contenido = File.ReadAllBytes(rutaArchivo); // Solo se hace una vez
            }
        }

        public static byte[] ObtenerCopia()
        {
            return (byte[])_contenido.Clone(); // Devuelve una copia segura
        }

        public ServicesDocuments() {

            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));
            getDataApi = ConfigurationManager.AppSettings["getDataApi"];
            procesingApi = ConfigurationManager.AppSettings["processApi"];
            confirmationApi = ConfigurationManager.AppSettings["confirmationApi"];
            plantillaGenerals = ConfigurationManager.AppSettings["plantillaGeneral"];
            deposito = ConfigurationManager.AppSettings["deposit"];
            plantillas = ConfigurationManager.AppSettings["templates"];
            generated = ConfigurationManager.AppSettings["generated"];
            plantilla = ConfigurationManager.AppSettings["plantilla"];
            temporales = ConfigurationManager.AppSettings["temporales"];
            rutaOriginal = System.IO.Path.Combine(plantillas, plantilla);
            manager=new PlantillaManager();
            manager.Inicializar(rutaOriginal);
            string rutaGeneral = System.IO.Path.Combine(plantillas, plantillaGenerals);
            general=new PlantillaGeneralManager();
            general.Inicializar(rutaGeneral);
            worker = new BackgroundWorker();
            worker.WorkerSupportsCancellation = true;
            worker.DoWork += Worker_DoWork;
            worker.RunWorkerCompleted += Worker_RunWorkerCompleted;

            
        }

        private static int liberacione;
        List<BackgroundWorker> workers = new List<BackgroundWorker>();

  

        public void Iniciar()
        {
            if (!worker.IsBusy)
                worker.RunWorkerAsync();
        }

        private void Worker_DoWork(object sender, DoWorkEventArgs e)
        {
            try
            {

                while (!worker.CancellationPending)
                {
                    // Aquí va la tarea que se repite
                    Console.WriteLine("Ejecutando tarea en segundo plano...");
                    log.Info("JC::::::::::::Comienza documentsGenerator Performance :::::::::::::JC 1.0.20");
                    DescargaContratos download = new DescargaContratos();
                    JObject serviceContract = download.recuperarDatosContratos();
                    List<Contrato> listaContratos = download.validarContratos(serviceContract);
                    int thread = Convert.ToInt32(ConfigurationManager.AppSettings["thread"]);


                log.Info("Hilos=== "+ ConfigurationManager.AppSettings["thread"]);

                if (thread==0)
                    thread=1;

                    if (listaContratos != null && listaContratos.Count > 0)
                    {

                        foreach (Contrato contrato in listaContratos)
                        {

                        

                         while (liberacione >= thread) {
                          Thread.Sleep(1000);    
                        }


                                BackgroundWorker workerInternal = new BackgroundWorker();
                            ProcesamientoContratos cc = new ProcesamientoContratos(download.GetToken());
                            workerInternal.DoWork += async (senderWorker, eWorker) =>
                            {
                                try
                                {
                                    cc.customerId = int.Parse(contrato.Valores.Where(x => x.Llave == "$$id_ente$$").Select(y => y.Valor).FirstOrDefault().ToString());
                                    cc.mail = contrato.Valores.Where(x => x.Llave == "$$correo$$").Select(y => y.Valor).FirstOrDefault().ToString();
                                    cc.dates = contrato.Valores.Where(x => x.Llave == "$$dates$$").Select(y => y.Valor).FirstOrDefault().ToString();

                                    log.Info(":::Comienza generación cliente:::  " + cc.customerId.ToString());

                                   var aws= cc.defineDocumento(contrato).GetAwaiter().GetResult();

                                }
                                catch
                                {

                                    log.Error("Error, información de cliente corrupta");
                                }
                                finally
                                {

                                  //  --liberacione;

                                }
                            };

                            workerInternal.RunWorkerCompleted += (senderWorker, eWorker) =>
                            {
                                --liberacione;
                                workers.Remove(workerInternal);

                            };

                            liberacione++;

                            workers.Add(workerInternal);
                            workerInternal.RunWorkerAsync();
                            Thread.Sleep(6000);


                        }

                    }
                    else
                    {
                        log.Info(":::::::::No existen contratos que procesar!!!!::::::::");
                        Thread.Sleep(30000);
                    }

                    break;
                }
            }
            catch (Exception xx) {


                log.Info("A WAIT TIME:::::::::::: No existen contratos que procesar");
                log.Error(xx);
                Thread.Sleep(30000);
            }
        }

        private void Worker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (!worker.CancellationPending)
            {
                Console.WriteLine("Reiniciando...");
                worker.RunWorkerAsync(); 
            }
        }

        public void documentsGenerator() {           

            try
            {
                
                log.Info("JC::::::::::::Comienza documentsGenerator :::::::::::::JC");

                log.Info("INICIAR VERSION 1.0.19.7 Document Services");

                List<Contrato> listaContratos;

                DescargaContratos download = new DescargaContratos();    

                JObject serviceContract = download.recuperarDatosContratos();


                listaContratos = download.validarContratos(serviceContract);

                log.Info("Comenzando a generar documentos");

                int thread = Convert.ToInt32(ConfigurationManager.AppSettings["thread"]);


                log.Info("Hilos=== "+ ConfigurationManager.AppSettings["thread"]);

                if (thread==0)
                    thread=1;

                if (listaContratos != null && listaContratos.Count > 0)
                {

                    foreach (Contrato contrato in listaContratos)
                    {                       
                            
                         while (liberacione >= thread) {
                          Thread.Sleep(3000);    

                        }              

                        BackgroundWorker worker = new BackgroundWorker();
                        ProcesamientoContratos cc = new ProcesamientoContratos(download.GetToken());
                        worker.DoWork += (senderWorker, eWorker) =>
                        {
                            try
                            {
                                cc.customerId = int.Parse(contrato.Valores.Where(x => x.Llave == "$$id_ente$$").Select(y => y.Valor).FirstOrDefault().ToString());
                                cc.mail = contrato.Valores.Where(x => x.Llave == "$$correo$$").Select(y => y.Valor).FirstOrDefault().ToString();
                                cc.dates = contrato.Valores.Where(x => x.Llave == "$$dates$$").Select(y => y.Valor).FirstOrDefault().ToString();

                                log.Info(":::Comienza generación cliente:::  " + cc.customerId.ToString());

                                cc.defineDocumento(contrato);
                                
                            }
                            catch
                            {

                                log.Error("Error, información de cliente corrupta");
                            }
                            finally
                            {

                                --liberacione;

                            }
                        };

                        worker.RunWorkerCompleted += (senderWorker, eWorker) =>
                        {
                            --liberacione;
                 
                        };



                        workers.Add(worker);

                        if(!worker.IsBusy)
                           worker.RunWorkerAsync();

                        liberacione++;

                    }
                }
                else {
                    log.Info(":::::::::No existen contratos que procesar!!!!::::::::");
                }
            } catch (Exception ex) {

                log.Error(ex);
            }

        }

        public void Cancelar()
        {
            if (worker.IsBusy)
                worker.CancelAsync();
        }





    }
}
