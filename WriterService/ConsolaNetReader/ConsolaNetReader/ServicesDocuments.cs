using log4net;
using log4net.Config;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;

namespace ConsolaNetReader
{
    public class ServicesDocuments
    {
        private static readonly ILog log = LogManager.GetLogger(typeof(ServicesDocuments));


        private static int liberacione;
        List<BackgroundWorker> workers = new List<BackgroundWorker>();

        public void documentsGenerator() {

            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));

            try
            {
                
                log.Info("JC::::::::::::Comienza documentsGenerator :::::::::::::JC");

                log.Info("INICIAR VERSION 14.0 Document Services 14.0");

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







    }
}
