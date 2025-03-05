#Crear el code.zip
zip -r code.zip lambda_function.py


#Ejecutar en ambiente Linux
# Crear el archivo requirements.txt
echo aws_secretsmanager_caching >> requirements.txt
echo paramiko >> requirements.txt
echo pymssql >> requirements.txt
 
# Crear un entorno virtual
python3.10 -m venv venv
 
# Activar el entorno virtual
source venv/bin/activate
 
# Instalar las dependencias
pip install -r requirements.txt
 
# Crear el directorio python y empaquetar las dependencias
mkdir -p python/lib/python3.10/site-packages
pip install -r requirements.txt -t python/lib/python3.10/site-packages
 
# Crear el archivo zip de la capa
zip -r layer.zip python
 
# Limpiar
deactivate
rm -rf venv
rm requirements.txt