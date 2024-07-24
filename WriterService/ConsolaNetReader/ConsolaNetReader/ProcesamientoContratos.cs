
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



namespace ConsolaNetReader
{


    public class ProcesamientoContratos
    {

        private static readonly ILog log = LogManager.GetLogger(typeof(ProcesamientoContratos));

        private static byte[] documento;
        // private const string directoryPath = "c:/cobis/contratos/";
        // private const string fileName = "plantilla.docx";
        private string deposito;
        private string plantillas;
        private string generated;
        private string token;
        private string plantilla;
        private string plantillaGeneral;
        private string commonFileName;
        private string fileNameDoc;
        private string fileNamePdf;
        private string guid;
        public string mail;
        public int customerId;
        private string client;
        private string secret;
        private string getDataApi;
        private string procesingApi;
        private string confirmationApi;
        private const string pdf = ".pdf";
        private const string doc = ".docx";



        public ProcesamientoContratos() {

            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));

            log.Info("JC :::::::::::::::Comienza ProcesamientoContratos::::::::::::::::: JC");

            try
            {
                this.client = ConfigurationManager.AppSettings["client"];
                this.secret = ConfigurationManager.AppSettings["secret"];
                this.getDataApi = ConfigurationManager.AppSettings["getDataApi"];
                this.procesingApi = ConfigurationManager.AppSettings["processApi"];
                this.confirmationApi = ConfigurationManager.AppSettings["confirmationApi"];
                this.plantillaGeneral = ConfigurationManager.AppSettings["plantillaGeneral"];

                string token = string.Empty;

                this.setFileName();

                var authUrl = ConfigurationManager.AppSettings["tokeneiser"];
                var clientId = this.client;
                var clientSecret = this.secret;

                this.deposito = ConfigurationManager.AppSettings["deposit"];
                this.plantillas = ConfigurationManager.AppSettings["templates"];
                this.generated = ConfigurationManager.AppSettings["generated"];
                this.plantilla = ConfigurationManager.AppSettings["plantilla"];

                log.Info("deposito " + deposito);
                log.Info("plantillas " + plantillas);
                log.Info("generated " + generated);
                log.Info("plantilla " + plantilla);


                 log.Info("Generando Token");

                var client = new RestClient(authUrl); 
                  var request = new RestRequest(authUrl, Method.Post);

                  request.AddParameter("grant_type", "client_credentials");
                  request.AddParameter("client_id", clientId);
                  request.AddParameter("client_secret", clientSecret);


                   RestResponse response = client.Execute(request);
                var tokenData = JObject.Parse(response.Content);
                this.token = tokenData["access_token"].ToString();

                log.Info("Token generado.....");
            }
            catch (Exception xe) {

                log.Error(xe);
            }

        }

        public void setFileName() {

            this.guid = Guid.NewGuid().ToString();
            this.fileNameDoc = string.Concat(guid, doc);
            this.fileNamePdf = string.Concat(guid, pdf);

        }

        public static byte[] Documento
        {
            get { return documento; }
            set { documento = value; }
        }



        public void defineDocumento(Contrato contratos) {

            try
            {
                log.Info("Comienza defineDocumento");

                Application wordApp = new Application();
                Microsoft.Office.Interop.Word.Document plantilla = null;


                try
                {
                    String rutaOriginal = System.IO.Path.Combine(plantillas, this.plantilla);
                    plantilla = wordApp.Documents.Open(rutaOriginal);
                    aplicaCambiosContrato(contratos, plantilla, "CONTRATO");
                    convertToPDF();
                    uploadFile("CONTRATO");


                } catch (Exception e) {
                    log.Error(e);
                } finally
                {

                    wordApp.Quit();
                }

                Application wordApp2 = new Application();
                Microsoft.Office.Interop.Word.Document plantillaGeneral = null;
                this.setFileName();

                try
                {
                    string rutaGeneral = System.IO.Path.Combine(plantillas, this.plantillaGeneral);
                    plantillaGeneral = wordApp2.Documents.Open(rutaGeneral);
                    aplicaCambiosContrato(contratos, plantillaGeneral, "DATOS CLIENTE");
                    convertToPDF();
                    uploadFile("DATOS CLIENTE");
                }
                catch (Exception xe)
                {
                    log.Error(xe);
                }
                finally {
                    wordApp2.Quit();
                }
            }
            catch (Exception xe) {
                log.Error(xe);
            }

        }




        private void uploadFile(string typeFile) {

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

                }
                else
                {
                    log.Info(typeFile + "::: Error al realizar carga");
                    log.Error(response);
                }
            }
            catch (Exception xe) {

                log.Error(xe);

            }




        }


        private void confirmaGeneraciónCarga(string type) {

            try
            {
                log.Info(type + " confirmaGeneraciónCarga ");
                var apiUrl = this.confirmationApi;

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
                else {
                    log.Info(type + "::: Error al confirmar");
                    log.Error(response);
                }
            }
            catch (Exception xe) {
                log.Error(xe);
            }
        }

        private void convertToPDF() {

            Microsoft.Office.Interop.Word.Document wordDocument = null;
            Microsoft.Office.Interop.Word.Application appWord = null;

            try
            {

                log.Info(" Comienza convertToPDF ");

                appWord = new Microsoft.Office.Interop.Word.Application();

                wordDocument = appWord.Documents.Open(deposito + fileNameDoc);

                wordDocument.ExportAsFixedFormat(generated + fileNamePdf, WdExportFormat.wdExportFormatPDF);

                log.Info(" Finaliza convertToPDF ");
            }
            catch (Exception xe)
            {
                log.Error(xe);
            }
            finally {
                wordDocument.Close();
                appWord.Quit();


            }
        }

        private string recuperaFecha() {

            DateTime fechaActual = DateTime.Now;
            string formato4 = fechaActual.ToString("dddd, dd MMMM yyyy", new CultureInfo("es-ES"));

            return formato4;
        }

        private void aplicaCambiosContrato(Contrato contratos, Microsoft.Office.Interop.Word.Document plantilla,string type)
        {

            string values = "";

            Application wordApp = null;

            try {

                 wordApp = new Application();

                log.Info(" Comienza aplicaCambiosContrato ");

                Valores fecha=new Valores();
                fecha.Llave = "$$fechacontrato$$";
                fecha.Valor = recuperaFecha();

                contratos.Valores.Add(fecha);

                 foreach (Valores valor in contratos.Valores){

                    values = valor.Valor;
                    if(!values.IsValueNullOrEmpty()&&values.Length>120)
                        values= values.Substring(0, 120);

                    if (values.IsValueNullOrEmpty())
                    {
                        values = "";
                    }

                        foreach (Range seleccion in plantilla.StoryRanges)
                        {

                            seleccion.Find.Execute(FindText: valor.Llave, ReplaceWith: values, MatchWildcards: false, Forward: true, Format: false, Wrap: WdFindWrap.wdFindContinue, Replace: WdReplace.wdReplaceAll,
                              MatchCase: false, MatchWholeWord: false, MatchSoundsLike: false, MatchAllWordForms: false, MatchKashida: false, MatchDiacritics: false, MatchAlefHamza: false,
                               MatchControl: false);
                        }
                }

                if (type.Equals("CONTRATO")) {

                    this.addBeneficiariesDocument(plantilla, contratos);
                }


                log.Info(" Finaliza cambios en plantilla ");

                plantilla.SaveAs2(deposito+ fileNameDoc);

                log.Info(" generación d earchivo exitoso ");

            }
            catch (Exception ex) {

                log.Error(ex);

            }
            finally {

                plantilla.Close();
                wordApp.Quit();



            }
      
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
                                row.Cells[2].Range.Text = beneficiario.domicilio;
                                DateTime fecha;
                                if (DateTime.TryParseExact(beneficiario.fechaNacimiento, "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture, DateTimeStyles.None, out fecha))
                                {
                                    string fechaFormateada = fecha.ToString("dd/MM/yyyy");
                                    row.Cells[3].Range.Text = fechaFormateada;
                                }
                                row.Cells[4].Range.Text = beneficiario.porcentaje;
                            }

                       

                        }
                        else {

                            for (int x = 0; x < 2; x++)
                            {
                                Microsoft.Office.Interop.Word.Row row = tabla.Rows.Add();
                                row.Cells[1].Range.Text = "No Designados";
                                row.Cells[2].Range.Text = "No Designados";
                                row.Cells[3].Range.Text = "No Designados";
                                row.Cells[4].Range.Text = "No Designados";
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


        public List<Contrato> validarContratos(JObject jsons)
        {

            List<Valores> listValores = new List<Valores>();
            List<Contrato> contra = new List<Contrato>();

            try
            {

                if (jsons != null)
                {

                    JArray datosPlantilla = (JArray)jsons["sp_bv_event_contract"];
                    JArray valoresPlantilla = (JArray)jsons["dato_plantilla_contract"];
    

                    log.Info(datosPlantilla);
                    log.Info(valoresPlantilla);


                    foreach (JObject dato in datosPlantilla)
                    {                       

                        Contrato contratos = new Contrato();

                        listValores = new List<Valores>();       

                        foreach (JObject maq in valoresPlantilla)
                        {

                            log.Info($"{maq.ToString()}");      

                            Valores val = new Valores();

                            val.Llave = maq["db_sustitucion"].ToString();
                            val.Valor = validadarValor(dato[maq["dp_relacion"].ToString()]);   

                            listValores.Add(val);
                        }

                        var beneficiarios=dato["beneficiarios"];

                        List<Beneficiario> listBeneficiarios = new List<Beneficiario>() ;


                        if (beneficiarios != null)
                        {
                            foreach (var beneficiario in beneficiarios)
                            {
                                if (beneficiario["Nombres"] != null)
                                {
                                    Beneficiario ben = new Beneficiario();
                                    ben.nombre = beneficiario["Nombres"].ToString();
                                    ben.paterno = beneficiario["paterno"].ToString();
                                    ben.materno = beneficiario["materno"].ToString();
                                    ben.domicilio = beneficiario["domicilio"].ToString();
                                    ben.fechaNacimiento = beneficiario["nacimiento"].ToString();
                                    ben.porcentaje = beneficiario["porcentaje"].ToString();
                                    listBeneficiarios.Add(ben);
                                }

                            }
                        }

                        contratos.Valores = listValores;
                        contratos.Beneficiarios=listBeneficiarios;
                        contra.Add(contratos);
                    }


                }
            }
            catch (Exception xe) { 
            
               log.Error(xe);       
            }

            return contra;
        }


        private  string validadarValor(object cadena)
        {

            string retorno = "";

            if (cadena!=null && !cadena.ToString().IsValueNullOrEmpty()) {
                retorno = cadena.ToString();
            }

            return retorno;
        }

        private void getObtainDataCustomer()
        {



        }


        public JObject recuperarDatosContratos() {
        JObject jsons = new JObject();
        try
        {
                log.Info("Comienza recuperarDatosContratos");
             
            string apiUrl = this.getDataApi;
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
            }catch (Exception xe) { 
            
                log.Error(xe);  
            
            }

            return jsons;

        }


    }
}
