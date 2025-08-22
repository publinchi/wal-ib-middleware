using log4net;
using log4net.Config;
using MigraDoc.DocumentObjectModel;
using Newtonsoft.Json.Linq;
using RestSharp;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class DescargaContratos
    {

        public int customerId;
        private string client;
        private string secret;
        private string getDataApi;
        private string token;
        private int expire;

        

        private static readonly ILog log = LogManager.GetLogger(typeof(ServicesDocuments));

        public int Expire { get => expire; set => expire = value; }

        public DescargaContratos() {


            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));

            log.Info("JC :::::::::::::::Comienza ProcesamientoContratos::::::::::::::::: JC");

            try
            {
                this.getDataApi = ConfigurationManager.AppSettings["getDataApi"];

                if (RefreshToken.MustBeeRefresh())
                {



                    token = string.Empty;

                    var authUrl = ConfigurationManager.AppSettings["tokeneiser"];
                    this.client = ConfigurationManager.AppSettings["client"];
                    this.secret = ConfigurationManager.AppSettings["secret"];
                    var clientId = this.client;
                    var clientSecret = this.secret;


                    log.Info("Generando Token");

                    var client = new RestClient(authUrl);
                    var request = new RestRequest(authUrl, Method.Post);

                    request.AddParameter("grant_type", "client_credentials");
                    request.AddParameter("client_id", clientId);
                    request.AddParameter("client_secret", clientSecret);


                    RestResponse response = client.Execute(request);
                    var tokenData = JObject.Parse(response.Content);                 
                    

                    RefreshToken.token = tokenData["access_token"].ToString();
                    RefreshToken.expire = Convert.ToInt32(tokenData["expires_in"]);


                    log.Info("Token generado.....");

                }

                this.token = RefreshToken.token;
                this.expire = RefreshToken.expire;
            }
            catch (Exception xe)
            {

                log.Error(xe);
            }


        }



        public string GetToken() {

            return this.token;
        }

        public List<Contrato> ConvertToObejctList(JObject jsons)
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

                            if (val.Llave.Equals("$$nombre$$"))
                            {
                                val.Valor = CapitalizeWords(validadarValor(dato[maq["dp_relacion"].ToString()]));
                            }
                            else
                            {

                                val.Valor = validadarValor(dato[maq["dp_relacion"].ToString()]);
                            }

                            listValores.Add(val);
                        }

                        var beneficiarios = dato["beneficiarios"];

                        List<Beneficiario> listBeneficiarios = new List<Beneficiario>();


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
                        contratos.Beneficiarios = listBeneficiarios;
                        contra.Add(contratos);
                    }


                }
            }
            catch (Exception xe)
            {

                log.Error(xe);
            }

            return contra;
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

        private string validadarValor(object cadena)
        {

            string retorno = "";

            if (cadena != null && !cadena.ToString().IsValueNullOrEmpty())
            {
                retorno = cadena.ToString();
            }

            return retorno;
        }

        public JObject recuperarDatosContratos()
        {
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
            }
            catch (Exception xe)
            {

                log.Error(xe);

            }

            return jsons;

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

                            if (val.Llave.Equals("$$nombre$$"))
                            {
                                val.Valor = CapitalizeWords(validadarValor(dato[maq["dp_relacion"].ToString()]));
                            }else if (val.Llave.Equals("$$nombrecompleto$$"))
                            {
                                val.Valor = CapitalizeWords(validadarValor(dato[maq["dp_relacion"].ToString()]));

                            }
                            else
                            {

                                val.Valor = validadarValor(dato[maq["dp_relacion"].ToString()]);
                            }




                            listValores.Add(val);
                        }

                        var beneficiarios = dato["beneficiarios"];

                        List<Beneficiario> listBeneficiarios = new List<Beneficiario>();


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
                        contratos.Beneficiarios = listBeneficiarios;
                        contra.Add(contratos);
                    }


                }
            }
            catch (Exception xe)
            {

                log.Error(xe);
            }

            return contra;
        }

    }
}
