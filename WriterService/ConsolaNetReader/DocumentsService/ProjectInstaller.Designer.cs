namespace DocumentsService
{
    partial class ProjectInstaller
    {
        /// <summary>
        /// Variable del diseñador necesaria.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Limpiar los recursos que se estén usando.
        /// </summary>
        /// <param name="disposing">true si los recursos administrados se deben desechar; false en caso contrario.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Código generado por el Diseñador de componentes

        /// <summary>
        /// Método necesario para admitir el Diseñador. No se puede modificar
        /// el contenido de este método con el editor de código.
        /// </summary>
        private void InitializeComponent()
        {
            this.ServiceContracts = new System.ServiceProcess.ServiceProcessInstaller();
            this.ContractsInstaller = new System.ServiceProcess.ServiceInstaller();
            // 
            // ServiceContracts
            // 
            this.ServiceContracts.Installers.AddRange(new System.Configuration.Install.Installer[] {
            this.ContractsInstaller});
            this.ServiceContracts.Password = null;
            this.ServiceContracts.Username = null;
            // 
            // ContractsInstaller
            // 
            this.ContractsInstaller.Description = "Contracts Service";
            this.ContractsInstaller.DisplayName = "ContractsService";
            this.ContractsInstaller.ServiceName = "Service1";
            this.ContractsInstaller.StartType = System.ServiceProcess.ServiceStartMode.Automatic;
            // 
            // ProjectInstaller
            // 
            this.Installers.AddRange(new System.Configuration.Install.Installer[] {
            this.ServiceContracts});

        }

        #endregion

        private System.ServiceProcess.ServiceProcessInstaller ServiceContracts;
        private System.ServiceProcess.ServiceInstaller ContractsInstaller;
    }
}