using ConsolaNetReader.CalixtaServices;
using log4net;
using log4net.Repository.Hierarchy;
using MigraDoc.DocumentObjectModel;
using RestSharp;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class ContractSend:GatewayPortTypeClient
    {

        private  readonly ILog log = LogManager.GetLogger(typeof(ContractSend));


        private string cuenta;
        private string nombre;
        private string horario;
        private string plantillaResulta;
        private string fileContract;
        private string mail;
        private string apiKey;

        public ContractSend()
        {

            this.fileContract = "C:\\Pruebas\\prueba.png";
            this.cuenta = "prueba";
            DateTime fecha = DateTime.Parse("01/01/1990");
            this.mail = "jolmossolis@gmail.com";
            this.plantillaResulta = Requeridos.getPlantillaMail();

            try
            {

                this.horario = String.Format("{0:dd/MM/yy} - Hora: {0:hh:mm tt}", fecha).ToLower();

            }
            catch (Exception xe) {
                log.Error(xe);
            }


        }


        public ContractSend(Contrato contrato,string fileContract)
        {
            this.cuenta=contrato.Valores.Where(x => x.Llave == "$$cuenta$$").Select(y => y.Valor).FirstOrDefault().ToString();
            this.nombre = contrato.Valores.Where(x => x.Llave == "$$nombre$$").Select(y => y.Valor).FirstOrDefault().ToString();
            string fechado = contrato.Valores.Where(x => x.Llave == "$$dates$$").Select(y => y.Valor).FirstOrDefault().ToString();
           // string apiKey = contrato.Valores.Where(x => x.Llave == "$$apiKey$$").Select(y => y.Valor).FirstOrDefault().ToString();

            try
            {
                if (apiKey.IsValueNullOrEmpty())
                {
                    this.apiKey = "c4b3624c-f458-9476-10cc-ce6879d8e66a";

                }

                DateTime fecha = DateTime.Parse(fechado);
                this.horario = String.Format("{0:dd/MM/yy} - Hora: {0:hh:mm tt}", fecha).ToLower();
            }catch (Exception xe) {
                this.horario = "";
               log.Error(xe);
            }
            this.fileContract = fileContract;
            this.mail= contrato.Valores.Where(x => x.Llave == "$$correo$$").Select(y => y.Valor).FirstOrDefault().ToString();
            maquetar();
        }


        public void maquetar() {

           string plantillaMail= Requeridos.getPlantillaMail();

            plantillaMail=plantillaMail.Replace("$$NOMBRE$$", this.nombre);
            plantillaMail=plantillaMail.Replace("$$CUENTA$$", this.cuenta);
            plantillaMail=plantillaMail.Replace("$$FECHADO$$", this.horario);

            plantillaResulta=plantillaMail;

        }

        public  async Task<bool> EnviaContratoAsync22() {

            log.Info("Enviando contrato ---- API REST " + this.mail);

            bool success = false;

            using (var client = new HttpClient())
            {

                try
                {

                    client.DefaultRequestHeaders.Add("apikey", this.apiKey);


                    var url = ConfigurationManager.AppSettings["UrlAuronix"];


                    var form = new MultipartFormDataContent
                    {
                        { new StringContent("CONTRATO"), "campaignName" },
                        { new StringContent(this.mail), "to" },
                        { new StringContent(ConfigurationManager.AppSettings["from"]), "from" },
                        { new StringContent(ConfigurationManager.AppSettings["fromName"]), "fromName" },
                        { new StringContent(ConfigurationManager.AppSettings["replyTo"]), "replyTo" },
                        { new StringContent(ConfigurationManager.AppSettings["subject"]), "subject" },
                        { new StringContent(plantillaResulta), "htmlEmail" }, 
                        { new StringContent(ConfigurationManager.AppSettings["fileName"]), "fileName" },
                        { new StringContent("true"), "removePreloadedFile" },
                        { new StringContent("true"), "selectAttachments" },
                        { new StringContent("false"), "includeEmbedImage" },
                        { new StringContent("false"), "sendWithoutAttachedFiles" }

                    };          


                    var fileStream = File.OpenRead(fileContract);
                    var streamContent = new StreamContent(fileStream);

          
                    form.Add(streamContent, "file", Path.GetFileName(fileContract));

     

                    var response = await client.PostAsync(url, form);

                    if (response.IsSuccessStatusCode)
                    {
                        var resultado = await response.Content.ReadAsStringAsync();
                 
                        Console.WriteLine(resultado);
                        log.Info(resultado);
                        success= true;
                        
                        
                    }
                    else
                    {
       
                        var errorContent = await response.Content.ReadAsStringAsync();
                        log.Error(errorContent);
                        success= false;

                    }
                }
                catch (Exception e) {

                    log.Info("Error enviando contrato");
                    log.Error(e);
                }

            }

            return success;

        }



        public async Task<bool> EnviaContratoAsync()
        {

            bool success = false;

            log.Info("Enviando contrato SOAP ---- "+ this.mail);



            byte[] array = File.ReadAllBytes(fileContract);

            CalixtaServices.EventosEnvioEmailRequest requestMail = new CalixtaServices.EventosEnvioEmailRequest();
            requestMail.cte = 49089;
            requestMail.id = "cobis@cuentacashi.com.mx";

            requestMail.password = "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5";
           CalixtaServices.GatewayPortTypeClient client = new CalixtaServices.GatewayPortTypeClient();
            client.Open();

            string idCalixta = await client.EnviaEmailAsync(49089, "cobis@cuentacashi.com.mx", "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5", "PRUEBA" +
                "", this.mail, "clientes@cuentacashi.com.mx", "Cuenta Cashi", "clientes@cuentacashi." +
                 "com.mx", "¡Tu Cuenta Cashi está lista!", 0, "", plantillaResulta, 1, array
                 , "Contrato.pdf", "Contrato.pdf", 0, "", 0, 0, "", "", "");
            
            
            client.Close();

            if (!String.IsNullOrEmpty(idCalixta))
            {
                success=true;
            }

            log.Info("Id Evio---- " + idCalixta);


            return success;
        }


    }
}
