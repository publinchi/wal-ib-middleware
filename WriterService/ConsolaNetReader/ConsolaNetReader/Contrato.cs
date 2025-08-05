using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public class Contrato
    {
       private List<Valores> valores;
       private List<Beneficiario> beneficiarios;
        private bool generado;
        public Contrato() { }

        public List<Valores> Valores { get => valores; set => valores = value; }

        public List<Beneficiario> Beneficiarios {

            get { return beneficiarios; }
            set { beneficiarios = value;}
        }

        public bool Generado { get => generado; set => generado = value; }
    }
}
