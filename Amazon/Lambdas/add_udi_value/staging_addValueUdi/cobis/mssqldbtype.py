from datetime import datetime as validateDate


class DBint:
    value: int = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, int):
            self.value = value
        else:
            raise ValueError('Value provided is not an DBint')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBint'
        return "<class '" + tipo + "'>"


class DBnumeric:
    value: float = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, (int, float)):
            self.value = float(value)
        else:
            raise ValueError('Value provided is not an DBnumeric')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBnumeric'
        return "<class '" + tipo + "'>"


class DBstr:
    value: str = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, str):
            self.value = value
        else:
            raise ValueError('Value provided is not an DBstr')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBstr'
        return "<class '" + tipo + "'>"


class DBdate:
    # Only support iso and SQL standard
    value: str = None
    inout: int = 0

    def __init__(self, value):
        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        value_error = False
        formato_fecha_sql_standar = "%Y.%m.%d"
        formats = ("%y.%m.%d", "%Y.%m.%d", "%y-%m-%d", "%Y-%m-%d", "%y-%m-%d", "%y-%m-%d", "%Y/%m/%d")
        contador = 0
        fecha = None
        for aformat in formats:
            try:
                fecha = validateDate.strptime(value, aformat)
                contador += 1
            except ValueError:
                pass
        if not fecha is None:
            self.value = fecha.strftime(formato_fecha_sql_standar)
        else:
            raise ValueError("Value not match with date datatype")
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBdate'
        return "<class '" + tipo + "'>"


class DBdatetime:
    # Only support iso and SQL standard
    value: str = None
    inout: int = 0

    def __init__(self, value):
        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        value_error = False
        formato_fecha_sql_standar = "%Y.%m.%d"
        formato_hora_estandar = "%H:%M:%S.%f"
        formats = ("%y.%m.%d", "%Y.%m.%d", "%y-%m-%d", "%Y-%m-%d", "%y-%m-%d", "%y-%m-%d", "%Y/%m/%d")
        contador = 0
        fecha = None
        for aformat in formats:
            try:
                a_format = aformat + " " + formato_hora_estandar
                fecha = validateDate.strptime(value, aformat)
                contador += 1
            except ValueError:
                pass
        if fecha is not None:
            self.value = fecha.strftime(formato_fecha_sql_standar + " " + formato_hora_estandar)
        else:
            raise ValueError("Value not match with datetime datatype")
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBdatetime'
        return "<class '" + tipo + "'>"


from datetime import datetime as validateDate


class DBint:
    value: int = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, int):
            self.value = value
        else:
            raise ValueError('Value provided is not an DBint')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBint'
        return "<class '" + tipo + "'>"


class DBnumeric:
    value: float = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, (int, float)):
            self.value = float(value)
        else:
            raise ValueError('Value provided is not an DBnumeric')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBnumeric'
        return "<class '" + tipo + "'>"


class DBstr:
    value: str = None
    inout: int = 0

    def __init__(self, value):

        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        if isinstance(value, str):
            self.value = value
        else:
            raise ValueError('Value provided is not an DBstr')
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBstr'
        return "<class '" + tipo + "'>"


class DBdate:
    # Only support iso and SQL standard
    value: str = None
    inout: int = 0

    def __init__(self, value):
        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        value_error = False
        formato_fecha_sql_standar = "%Y.%m.%d"
        formats = ("%y.%m.%d", "%Y.%m.%d", "%y-%m-%d", "%Y-%m-%d", "%y-%m-%d", "%y-%m-%d", "%Y/%m/%d")
        contador = 0
        fecha = None
        for aformat in formats:
            try:
                fecha = validateDate.strptime(value, aformat)
                contador += 1
            except ValueError:
                pass
        if not fecha is None:
            self.value = fecha.strftime(formato_fecha_sql_standar)
        else:
            raise ValueError("Value not match with date datatype")
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBdate'
        return "<class '" + tipo + "'>"


class DBdatetime:
    # Only support iso and SQL standard
    value: str = None
    inout: int = 0

    def __init__(self, value):
        if value is not None:
            inout = 0  # input
        else:
            inout = 1  # output
            return

        value_error = False
        formato_fecha_sql_standar = "%Y.%m.%d"
        formato_hora_estandar = "%H:%M:%S.%f"
        formats = ("%y.%m.%d", "%Y.%m.%d", "%y-%m-%d", "%Y-%m-%d", "%y-%m-%d", "%y-%m-%d", "%Y/%m/%d")
        contador = 0
        fecha = None
        for aformat in formats:
            try:
                a_format = aformat + " " + formato_hora_estandar
                fecha = validateDate.strptime(value, aformat)
                contador += 1
            except ValueError:
                pass
        if fecha is not None:
            self.value = fecha.strftime(formato_fecha_sql_standar + " " + formato_hora_estandar)
        else:
            raise ValueError("Value not match with datetime datatype")
        if inout not in (0, 1):
            raise ValueError('Incorrect type of parameter')
        else:
            self.inout = inout

    def gettype(self):
        tipo = 'DBdatetime'
        return "<class '" + tipo + "'>"
