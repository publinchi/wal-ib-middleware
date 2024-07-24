
using log4net.Config;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace ServiceContractInstaller
{
    internal static class Program
    {
        /// <summary>
        /// Punto de entrada principal para la aplicación.
        /// </summary>
        static void Main()
        {
               ServiceBase[] ServicesToRun;
                ServicesToRun = new ServiceBase[]
                {
                    new Service1()
                };
                ServiceBase.Run(ServicesToRun);
            

        

     /*       List<Contratos> listaContratos;
            ProcesamientoContratos cc = new ProcesamientoContratos();

            JObject serviceContract = cc.recuperarDatosContratos();


            listaContratos = cc.validarContratos(serviceContract);

            foreach (Contratos contrato in listaContratos)
            {
                cc.defineDocumento(contrato);

            }

            */

        }
    }
}
