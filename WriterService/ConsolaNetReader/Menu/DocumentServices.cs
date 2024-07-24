using ConsolaNetReader;
using log4net;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;
using System.Windows.Forms;

namespace DocumentServices
{
    public partial class DocumentServices : Form
    {

        private static readonly ILog log = LogManager.GetLogger(typeof(DocumentServices));

        System.Timers.Timer timer;

        public DocumentServices()
        {
            log4net.Config.XmlConfigurator.Configure();
            using (Mutex mutex = new Mutex(false, "Menu", out bool isCreatedNew))
            {
                if (!isCreatedNew)
                {
                    Environment.Exit(0);
                }
            }
            using (Mutex mutex = new Mutex(false, "DocumentsServices", out bool isCreatedNew))
            {
                if (!isCreatedNew)
                {
                    Environment.Exit(0);
                }
            }
            InitializeComponent();
            using (Mutex mutex = new Mutex(false, "DocumentsServices", out bool isCreatedNew))
            {
                if (!isCreatedNew)
                {
                    Environment.Exit(0);
                }
            }

        }

        private void Form1_Load(object sender, EventArgs e)
        {

            foreach (var process in Process.GetProcessesByName("WINWORD"))
            {
                try
                {
                    process.Kill();
                    process.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Error al cerrar el proceso: " + ex.Message);
                }
            }

                log4net.Config.XmlConfigurator.Configure();
            string espera = ConfigurationManager.AppSettings["leadTime"];
            string deposito = ConfigurationManager.AppSettings["deposit"];
            Directory.CreateDirectory(deposito);
            string generated = ConfigurationManager.AppSettings["generated"];
            Directory.CreateDirectory(generated);
            string templates = ConfigurationManager.AppSettings["templates"];
            Directory.CreateDirectory(templates);

 
            ServicesDocuments doc = new ServicesDocuments();
            doc.documentsGenerator();

            
            timer = new System.Timers.Timer();
            timer.Interval = int.Parse(espera); 
            timer.Elapsed += Timer_Elapsed;
            timer.Start();
        }

        private void Timer_Elapsed(object sender, ElapsedEventArgs e)
        {
            ServicesDocuments doc = new ServicesDocuments();
            doc.documentsGenerator();

        }

        private void cerrarToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void cerrarToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            Application.Exit(); 
        }
    }
}
