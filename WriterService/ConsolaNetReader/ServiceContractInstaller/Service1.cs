
using log4net;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

[assembly: log4net.Config.XmlConfigurator(ConfigFile = "App.config", Watch = true)]

namespace ServiceContractInstaller
{
    public partial class Service1 : ServiceBase
    {

        private Timer timer;

        private static readonly ILog log = LogManager.GetLogger(typeof(Service1));

        public Service1()
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args) { 


        log4net.Config.XmlConfigurator.Configure();

           string espera= ConfigurationManager.AppSettings["leadTime"];      
            string deposito= ConfigurationManager.AppSettings["deposit"];
            Directory.CreateDirectory(deposito);
            string generated= ConfigurationManager.AppSettings["generated"];
            Directory.CreateDirectory(generated);
            string templates= ConfigurationManager.AppSettings["templates"];
            Directory.CreateDirectory(templates);

            timer = new Timer();
            timer.Interval = int.Parse(espera); // Intervalo en milisegundos (por ejemplo, cada 1 minuto)
            timer.Elapsed += Timer_Elapsed;
            timer.Start();

        }

        protected override void OnStop()
        {
            timer.Stop();
            timer.Dispose();
        }

        private void Timer_Elapsed(object sender, ElapsedEventArgs e)
        {
            // Código para ejecutar cada vez que el temporizador se active
            // Por ejemplo, aquí podrías llamar a tu proceso

        }

        private void procesaContratos()
        {
            // Código para ejecutar tu proceso
        }


    }
}
