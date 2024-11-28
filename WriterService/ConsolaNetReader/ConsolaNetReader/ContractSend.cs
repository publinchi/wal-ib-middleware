using ConsolaNetReader.CalixtaServices;
using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class ContractSend:GatewayPortTypeClient
    {

        private static readonly ILog log = LogManager.GetLogger(typeof(ContractSend));


        private string cuenta;
        private string nombre;
        private string horario;
        private string plantillaResulta;
        private string plantillaBase;
        private string fileContract;
        private string mail;

        public ContractSend()
        {
            this.fileContract = "C:\\Pruebas\\prueba.png";
            this.cuenta = "prueba";
            DateTime fecha = DateTime.Parse("01/01/1990");
            this.horario = String.Format("{0:dd/MM/yy} - Hora: {0:hh:mm tt}", fecha).ToLower();
            this.mail = "jolmossolis@hotmail.com";
            this.plantillaResulta = "<html>HOLA MUNDO</html";



        }


        public ContractSend(Contrato contrato,string fileContract)
        {
            this.cuenta=contrato.Valores.Where(x => x.Llave == "$$cuenta$$").Select(y => y.Valor).FirstOrDefault().ToString();
            this.nombre = contrato.Valores.Where(x => x.Llave == "$$nombre$$").Select(y => y.Valor).FirstOrDefault().ToString();
            string fechado = contrato.Valores.Where(x => x.Llave == "$$dates$$").Select(y => y.Valor).FirstOrDefault().ToString();
            DateTime fecha = DateTime.Parse(fechado);
            this.horario = String.Format("{0:dd/MM/yy} - Hora: {0:hh:mm tt}", fecha).ToLower();
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


        public void EnviaContrato() {

            log.Info("Enviando contrato---- "+ this.mail);



            byte[] array = File.ReadAllBytes(fileContract);

            CalixtaServices.EventosEnvioEmailRequest requestMail = new CalixtaServices.EventosEnvioEmailRequest();
            requestMail.cte = 49089;
            requestMail.id = "cobis@cuentacashi.com.mx";

            requestMail.password = "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5";
           CalixtaServices.GatewayPortTypeClient client = new CalixtaServices.GatewayPortTypeClient();
            client.Open();

            string salidax = client.EnviaEmail(49089, "cobis@cuentacashi.com.mx", "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5", "PRUEBA" +
                "", this.mail, "clientes@cuentacashi.com.mx", "Cuenta Cashi", "clientes@cuentacashi." +
                 "com.mx", "¡Tu Cuenta Cashi está lista!", 0, "", plantillaResulta, 1, array
                 , "Contrato.pdf", "Contrato.pdf", 0, "", 0, 0, "", "", "");


         //   TransaccionEnvios status = new TransaccionEnvios();
         //   status.idEnvio = int.Parse(salidax);

         //   EdoMensajes[] mensajes = client.EstadoMensajes(status);
         //   var x= client.EstadoEnvio(status);


         //  int values= client.EstadoEnvioEmail(49089, "cobis@cuentacashi.com.mx", "dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5",int.Parse(salidax));


            
            
            client.Close();
            

            Console.WriteLine(salidax);
            Console.ReadLine();


        }


    }
}
