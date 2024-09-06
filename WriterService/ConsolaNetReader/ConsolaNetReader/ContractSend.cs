using ConsolaNetReader.CalixtaServices;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class ContractSend
    {
        public ContractSend()
        {


        }

        public void EnviaContrato() {


            byte[] array = File.ReadAllBytes("C:\\cobis\\pdf\\8c3936ad-f10c-4f0c-9f36-a68f62e1b90c.pdf");

            CalixtaServices.EventosEnvioEmailRequest requestMail = new CalixtaServices.EventosEnvioEmailRequest();
            requestMail.cte = 49089;
            requestMail.id = "cobis@cuentacashi.com.mx";
            requestMail.password = "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5";
            CalixtaServices.GatewayPortTypeClient client = new CalixtaServices.GatewayPortTypeClient();
            client.Open();
            string salidax = client.EnviaEmail(49089, "cobis@cuentacashi.com.mx", "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5", "", "jolmossolis@gmail.com"
                 , "no-replay@cuentacashi.com.mx", "Prueba Conratos", "", "Envio de contrato", 0, "", "<htlml>Hola mundo</html>", 1, array
                 , "Contratito.pdf", "", 0, "", 0, 0, "", "", "");


            TransaccionEnvios status = new TransaccionEnvios();
            status.idEnvio = int.Parse(salidax);

            EdoMensajes[] mensajes = client.EstadoMensajes(status);
            var x= client.EstadoEnvio(status);


           int values= client.EstadoEnvioEmail(49089, "cobis@cuentacashi.com.mx", "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5",int.Parse(salidax));


            client.Close();


            Console.WriteLine(salidax);
            Console.ReadLine();


        }


    }
}
