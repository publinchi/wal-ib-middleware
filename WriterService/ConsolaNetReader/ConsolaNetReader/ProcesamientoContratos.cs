
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using iTextSharp.text.pdf;


using Newtonsoft.Json.Linq;
using PdfSharp.Pdf;
using RestSharp;

using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xceed.Words.NET;

using MigraDoc.DocumentObjectModel;
using MigraDoc.Rendering;
using Document = MigraDoc.DocumentObjectModel.Document;
using Paragraph = DocumentFormat.OpenXml.Wordprocessing.Paragraph;
using Text = DocumentFormat.OpenXml.Wordprocessing.Text;
using Microsoft.Office.Interop.Word;


using System.Reflection;
using DocumentFormat.OpenXml.Spreadsheet;
using DocumentFormat.OpenXml.Vml;
using Newtonsoft.Json;
using System;
using log4net;
using System.Configuration;
using log4net.Config;
using System.Globalization;
using DocumentFormat.OpenXml.Office2010.Excel;
using System.Diagnostics.Contracts;
using System.Diagnostics;
using System.Runtime.InteropServices;



namespace ConsolaNetReader
{


    public class ProcesamientoContratos: ServicesDocuments
    {

        private static readonly ILog log = LogManager.GetLogger(typeof(ProcesamientoContratos));

        private static byte[] documento;
       
        private string token;
  
        private string commonFileName;
        private string fileNameDoc;
        private string fileNamePdf;
        private string temporalFile;
        private string temporalFileGeneral;
        private string guid;
        public string mail;
        public string dates;
        public int customerId;
        private string client;
        private string secret;




        public ProcesamientoContratos(string token)
        {

            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));

            log.Info("JC :::::::::::::::Comienza ProcesamientoContratos::::::::::::::::: JC");

            try
            {
                this.client = ConfigurationManager.AppSettings["client"];
                this.secret = ConfigurationManager.AppSettings["secret"];


                this.token = token;

                this.setFileName();

                var authUrl = ConfigurationManager.AppSettings["tokeneiser"];
                var clientId = this.client;
                var clientSecret = this.secret;

                log.Info("deposito " + deposito);
                log.Info("plantillas " + plantillas);
                log.Info("generated " + generated);
                log.Info("plantilla " + plantilla);

            }
            catch (Exception xe)
            {

                log.Error(xe);
            }

        }

        public void setFileName()
        {
            this.guid = Guid.NewGuid().ToString();

            this.fileNameDoc = string.Concat(Guid.NewGuid().ToString(), doc);
            this.fileNamePdf = string.Concat(Guid.NewGuid().ToString(), pdf);
            this.temporalFile = string.Concat(Guid.NewGuid().ToString(), doc);
            this.temporalFileGeneral = string.Concat(Guid.NewGuid().ToString(), doc);

        }



        public static byte[] Documento
        {
            get { return documento; }
            set { documento = value; }
        }



        public async Task<bool> defineDocumento(Contrato contratos)
        {

            string contractToMail = null;

            bool flagContrato = false;
            bool flagDatosGenerales = false;
            bool success=true;

            try
            {
                log.Info("Comienza defineDocumento");

                Application wordApp = new Application();
                Microsoft.Office.Interop.Word.Document plantilla = null;


                string contract = contratos.Valores.Where(x => x.Llave == "$$contract$$").Select(y => y.Valor).FirstOrDefault().ToString();

                if (!contract.Equals("V"))
                {

                    try
                    {


                        /*  String rutaOriginal = System.IO.Path.Combine(plantillas, base.plantilla);
                          File.Copy(rutaOriginal, System.IO.Path.Combine(temporales, this.temporalFile), overwrite: true);
                          plantilla = wordApp.Documents.Open(System.IO.Path.Combine(temporales, this.temporalFile));*/

                        string rutaTemporal = base.manager.CrearCopiaTemporal();
                        plantilla = wordApp.Documents.Open(rutaTemporal);
                        aplicaCambiosContrato(contratos, plantilla, "CONTRATO");
                        contractToMail = convertToPDF();
                        if (contractToMail != "ERROR")
                        {
                            flagContrato = true;
                            ContractSend mail = new ContractSend(contratos, contractToMail);
                            bool sender = await mail.EnviaContratoAsync();
                            if (sender)
                            {
                                try
                                {
                                    uploadFile("CONTRATO");
                                }
                                catch (Exception ex)
                                {
                                    log.Info("Error al enviar el correo");
                                    log.Error(ex);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        log.Error(e);
                    }
                    finally
                    {
                        wordApp.Quit();
                        Marshal.ReleaseComObject(wordApp);

                        File.Delete(System.IO.Path.Combine(temporales, this.temporalFile));
                    }


                } else
                    flagContrato = true;


                string generalData = contratos.Valores.Where(x => x.Llave == "$$generaldata$$").Select(y => y.Valor).FirstOrDefault().ToString();

                if (!generalData.Equals("1"))
                {
                    Application wordApp2 = new Application();
                    Microsoft.Office.Interop.Word.Document plantillaGeneral = null;
                    this.setFileName();

                    try
                    {

                        plantillaGeneral = wordApp2.Documents.Open(general.CrearCopiaTemporal());
                        aplicaCambiosContrato(contratos, plantillaGeneral, "DATOS CLIENTE");

                        convertToPDF();
                        uploadFile("DATOS CLIENTE");
                        flagDatosGenerales = true;
                    }
                    catch (Exception xe)
                    {
                        log.Error(xe);
                    }
                    finally
                    {
                        wordApp2.Quit();
                        Marshal.ReleaseComObject(wordApp);
                        File.Delete(System.IO.Path.Combine(temporales, this.temporalFileGeneral));
                    }
                } else
                    flagDatosGenerales = true;

            }
            catch (Exception xe)
            {
                success = false;
                log.Error(xe);
            }

            return success;

        }




        private void uploadFile(string typeFile)
        {

            try
            {

                log.Info("Comienza uploadFile");

                var apiUrl = procesingApi;

                byte[] file = File.ReadAllBytes(generated + fileNamePdf);

                string fileContent = Convert.ToBase64String(file);

                var json = new
                {
                    blobPdf = fileContent,
                    mail = this.mail,
                    externalCustomerId = this.customerId,
                    typeFile = typeFile
                };

                string jsonString = JsonConvert.SerializeObject(json);

                WebClient client = new WebClient();
                string bearerToken = this.token;
                client.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + bearerToken);
                client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
                string response = client.UploadString(apiUrl, jsonString);


                if (response.Contains("success"))
                {
                    log.Info(typeFile + "::: Se cargo existosamente");
                    confirmaGeneraciónCarga(typeFile);
                    File.Delete(generated + fileNamePdf);

                }
                else
                {
                    log.Info(typeFile + "::: Error al realizar carga");
                    log.Error(response);
                }
            }
            catch (Exception xe)
            {

                log.Error(xe);

            }




        }


        private void confirmaGeneraciónCarga(string type)
        {

            try
            {
                log.Info(type + " confirmaGeneraciónCarga ");
                var apiUrl = confirmationApi;

                var json = new
                {
                    externalCustomerId = this.customerId,
                    typeFile = type
                };

                string jsonString = JsonConvert.SerializeObject(json);

                WebClient client = new WebClient();
                string bearerToken = this.token;
                client.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + bearerToken);
                client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
                string response = client.UploadString(apiUrl, jsonString);

                if (response.Contains("success"))
                {

                    log.Info(type + "::: Se confirmo existosamente");
                }
                else
                {
                    log.Info(type + "::: Error al confirmar");
                    log.Error(response);
                }
            }
            catch (Exception xe)
            {
                
                log.Error(xe);
            }
        }

        private string convertToPDF()
        {

            Microsoft.Office.Interop.Word.Document wordDocument = null;
            Microsoft.Office.Interop.Word.Application appWord = null;

            string contract = null;
            bool close = true;

            try
            {

                log.Info(" Comienza convertToPDF ");

                appWord = new Microsoft.Office.Interop.Word.Application();

                if (File.Exists(deposito + fileNameDoc)) {

                    wordDocument = appWord.Documents.Open(deposito + fileNameDoc);

                    wordDocument.ExportAsFixedFormat(generated + fileNamePdf, WdExportFormat.wdExportFormatPDF);

                    contract = generated + fileNamePdf;

                    log.Info(" Finaliza convertToPDF ");
                }
                else {
                    log.Info("No existe ruta imposible geranerar PDF " + deposito + fileNameDoc);
                    contract = "ERROR";
                     close = false;


                }
            }
            catch (Exception xe)
            {
                log.Error(xe);
            }
            finally
            {
                if (close)
                {
                    wordDocument.Close();
                    appWord.Quit();
                    File.Delete(deposito + fileNameDoc);
                }
            }

            return contract;
        }

        private string recuperaFecha()
        {

            string formato4 = "";

            try
            {

                DateTime fechaActual = DateTime.Now;
                formato4 = fechaActual.ToString("dddd, dd MMMM yyyy", new CultureInfo("es-ES"));


            }
            catch (Exception)
            {
                log.Error("Error al recuperar fecha");


            }

            return formato4;
        }



    
        public bool validarDatosContratros(Contrato contratos)
        {

            bool validacion = false;

            try
            {
                foreach (Valores valor in contratos.Valores)
                {
                    string values = valor.Valor;

                    if (values.IsValueNullOrEmpty())
                    {
                        values = "";
                    }
                }

            }
            catch (Exception xe)
            {

                throw;
            }

            return validacion;


        }

        private void aplicaCambiosContrato(Contrato contratos, Microsoft.Office.Interop.Word.Document plantilla, string type)
        {

            string values = "";

            Application wordApp = null;

            Requeridos.cliente = this.customerId;

            try
            {

                wordApp = new Application();

                log.Info(" Comienza aplicaCambiosContrato ");

                Valores fecha = new Valores();
                fecha.Llave = "$$fechacontrato$$";
                fecha.Valor = recuperaFecha();
                contratos.Valores.Add(fecha);



               foreach (Valores valor in contratos.Valores)
                {

                    values = valor.Valor;
                    if (!values.IsValueNullOrEmpty() && values.Length > 120)
                        values = values.Substring(0, 120);

                    if (values.IsValueNullOrEmpty())
                    {
                        values = "";
                    }


                    if (type.Equals("DATOS CLIENTE"))
                    {

                        if (!valor.Llave.IsValueNullOrEmpty() && valor.Llave.Equals("$$fechanacimiento$$")) {

                            try
                            {

                                DateTime fechaDatos = DateTime.Parse(valor.Valor);
                                log.Info("Fecha Nacimiento 1::: " + fechaDatos.ToString());
                                values = fechaDatos.ToString("yyyy-MM-dd");
                                log.Info("Fecha Nacimiento 2::: " + valor.Valor);

                            }
                            catch
                            {

                                log.Error("Error parser fecha nacimiento");
                            }

                        }

                    }

                    if (!Requeridos.validarValor(valor.Llave, values, type))
                    {
                        throw new Exception("CAMPO REQUERIDO 789DX");
                    }

                    foreach (Range seleccion in plantilla.StoryRanges)
                    {

                        /* seleccion.Find.Execute(FindText: valor.Llave, ReplaceWith: values, MatchWildcards: false, Forward: true, Format: false, Wrap: WdFindWrap.wdFindContinue, Replace: WdReplace.wdReplaceAll,
                           MatchCase: true, MatchWholeWord: false, MatchSoundsLike: false, MatchAllWordForms: false, MatchKashida: false, MatchDiacritics: false, MatchAlefHamza: false,
                            MatchControl: false);*/

                        seleccion.Find.ClearFormatting();
                        seleccion.Find.Replacement.ClearFormatting();

                        seleccion.Find.Execute(
                            FindText: valor.Llave,
                            ReplaceWith: values ?? string.Empty,
                            Replace: WdReplace.wdReplaceAll,
                            MatchCase: false,
                            MatchWholeWord: false,
                            MatchWildcards: false
                        );


                    }

    
                }

                if (type.Equals("CONTRATO"))
                {

                    this.addBeneficiariesDocument(plantilla, contratos);
                }


                log.Info(" Finaliza cambios en plantilla ");

                plantilla.SaveAs(deposito + fileNameDoc);

                log.Info(" generación d earchivo exitoso ");

            }
            catch (Exception ex)
            {



                log.Error(ex);

                if (ex.Message.Contains("789DX"))
                {
                    throw ex;
                }

            }
            finally
            {

                plantilla.Close();
                wordApp.Quit();

            }

        }

        public string CapitalizeWords(string input)
        {
            if (string.IsNullOrWhiteSpace(input))
                return input;

            // Divide la cadena en palabras
            var words = input.Split(' ');

            // Capitaliza cada palabra
            for (int i = 0; i < words.Length; i++)
            {
                if (words[i].Length > 0)
                {
                    words[i] = char.ToUpper(words[i][0]) + words[i].Substring(1).ToLower();
                }
            }

            // Une las palabras de nuevo en una sola cadena
            return string.Join(" ", words);
        }

        public void addBeneficiariesDocument(Microsoft.Office.Interop.Word.Document documento, Contrato contrato)
        {

            int i = 1;

            try
            {

                foreach (Microsoft.Office.Interop.Word.Table tabla in documento.Tables)
                {

                    if (i == 3)
                    {
                        if (contrato.Beneficiarios.Count > 0)
                        {

                            foreach (Beneficiario beneficiario in contrato.Beneficiarios)
                            {

                                Microsoft.Office.Interop.Word.Row row = tabla.Rows.Add();

                                row.Cells[1].Range.Text = beneficiario.nombre + " " + beneficiario.paterno + beneficiario.materno;
                                //row.Cells[2].Range.Text = beneficiario.domicilio;
                                DateTime fecha;
                                if (DateTime.TryParseExact(beneficiario.fechaNacimiento, "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture, DateTimeStyles.None, out fecha))
                                {
                                    string fechaFormateada = fecha.ToString("dd/MM/yyyy");
                                    row.Cells[2].Range.Text = fechaFormateada;
                                }
                                row.Cells[3].Range.Text = beneficiario.porcentaje;
                            }

                        }
                        else
                        {

                            for (int x = 0; x < 2; x++)
                            {
                                Microsoft.Office.Interop.Word.Row row = tabla.Rows.Add();
                                row.Cells[1].Range.Text = "No Designados";
                                //row.Cells[2].Range.Text = "No Designados";
                                row.Cells[2].Range.Text = "No Designados";
                                row.Cells[3].Range.Text = "No Designados";
                            }

                        }

                        break;
                    }
                    ++i;
                }



            }
            catch (Exception xe)
            {
                log.Info("Error beneficiaries Contract");
                log.Error(xe);

            }
        }




        private string validadarValor(object cadena)
        {

            string retorno = "";

            if (cadena != null && !cadena.ToString().IsValueNullOrEmpty())
            {
                retorno = cadena.ToString();
            }

            return retorno;
        }

        private void getObtainDataCustomer()
        {



        }


        public JObject recuperarDatosContratos()
        {
            JObject jsons = new JObject();
            try
            {
                log.Info("Comienza recuperarDatosContratos");

                string apiUrl = getDataApi;
                WebClient client = new WebClient();
                string bearerToken = this.token;
                client.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + bearerToken);
                client.Headers.Add(HttpRequestHeader.ContentType, "text/plain");
                string response = client.UploadString(apiUrl, "");

                log.Info(response);

                jsons = JObject.Parse(response);



            }
            catch (WebException e)
            {

                log.Error(e);

                if (e.Response is HttpWebResponse httpResponse)
                {

                    Console.WriteLine("La solicitud no fue exitosa. Código de estado: " + httpResponse.StatusCode);
                }
                else
                {

                    Console.WriteLine("Error al realizar la solicitud HTTP: " + e.Message);
                }
                recuperarDatosContratos();
            }


            return jsons;

        }


    }
}
