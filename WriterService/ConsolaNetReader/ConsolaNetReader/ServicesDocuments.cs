using log4net;
using log4net.Config;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class ServicesDocuments
    {
        private static readonly ILog log = LogManager.GetLogger(typeof(ServicesDocuments));

        public void  documentsGenerator() {

            XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));

            try
            {
                log.Info("JC::::::::::::Comienza documentsGenerator :::::::::::::JC");

                log.Info("INICIAR VERSION 7.0 Document Services 7.0");

                List<Contrato> listaContratos;
                ProcesamientoContratos cc = new ProcesamientoContratos();

                JObject serviceContract = cc.recuperarDatosContratos();


                listaContratos = cc.validarContratos(serviceContract);

                log.Info("Comenzando a generar documentos");

                if (listaContratos != null && listaContratos.Count > 0)
                {

                    foreach (Contrato contrato in listaContratos)
                    {
                        try
                        {
                            cc.customerId = int.Parse(contrato.Valores.Where(x => x.Llave == "$$id_ente$$").Select(y => y.Valor).FirstOrDefault().ToString());
                            cc.mail = contrato.Valores.Where(x => x.Llave == "$$correo$$").Select(y => y.Valor).FirstOrDefault().ToString();

                            log.Info(":::Comienza generación cliente:::  " + cc.customerId.ToString());

                            cc.defineDocumento(contrato);
                        }catch {

                            log.Error("Error, información de cliente corrupta");
                        }  

                    }
                }
                else {
                    log.Info(":::::::::No existen contratos que procesar!!!!::::::::");
                }
            }catch (Exception ex) {

                log.Error(ex);  
            }

        }

    }
}
