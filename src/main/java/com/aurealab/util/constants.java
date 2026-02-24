package com.aurealab.util;

public class constants {
    public static class messages {
        public static final String error = "Ha ocurrido un error: ";
        public static final String noData = "No encontró ningún dato";
        public static final String consultGood = "Consultado correctamente";
        public static final String responseSaveUserGood = "Datos creado correctamente";
        public static final String responseUpdateGood = "Datos actualizados correctamente";
        public static final String dontFoundByID = "No encontro ningun dato con ese ID";
        public static final String switchDTOToEntityError = "Error al convertir DTO a Entity: el objeto es nulo.";
        public static final String asd = "No hay sesión activa para realizar esta acción";
        public static final String cashSessionExist = "Ya hay una caja abierta para el dia de hoy";
        public static final String cashSessionDontExist = "No hay una caja abierta realizar esta acción";
    }
    public static class success{
        public static final String savedSuccess = "Creado exitosamente ";
        public static final String loginSuccess = "Sesion Iniciada correctamente ";
        public static final String findedSuccess = "busqueda completada ";
        public static final String updatedSuccess = "Actualizado exitosamente";
        public static final String overedSuccess = "Proceso terminado satisfactoriamente";
    }
    public static class errors{
        public static final String saveError = "Error al intentar crear";
        public static final String loginError = "Error, Usuario o contraseña invalida";
        public static final String updateError = "Error al intentar actualizar";
        public static final String findError = "Error al intentar buscar";
        public static final String routeNotFound = "Error, ruta no encontrada";
        public static final String handlerException = "Ocurrió un error inesperado.";
        public static final String errorCreatingToken = "Ocurrió un error al crear el token";
        public static final String internalServerError = "INTERNAL_SERVER_ERROR";
        public static final String tokenCreationError = "TOKEN_CREATION_ERROR";
        public static final String tokenValidationError = "Token invalido, No autorizado.";
        public static final String unespectedError = "Error inesperado ";
        public static final String invalidUserOrPass = "Usuario o contraseña invalido";
        public static final String dataPersistenceError = "Datos duplicados";
        public static final String invalidRole = "No se encontró ningún rol asociado al validador";
        public static final String invalidUser = "No se encontró ningún usuario con esas credenciales";
        public static final String invalidMenu = "No se encontró ningún menu asociado al validador";
    }

    public static class descriptions {
        public static final String superUser = "Este rol tiene permisos globales sobre toda la aplicación";
        public static final String admin = "Este rol tiene privilegios elevados";
        public static final String supervisor = "Este rol esta mas enfocado en reportes y metricas";
        public static final String operativeUser = "Usuario operativo con capacidad de gestionar turnos";
        public static final String digiter = "Este rol solo ingresará datos ";
        public static final String loginError = "Las credenciales suministradas no pertenecen a ningun usuario";
        public static final String empty = "El sistema no encontro ningun dato en la base de datos";
        public static final String configParams = "Error leyendo los parametros de configuracion";
    }

    public static class roles{
        public static final String advisor = "asesor";
    }

    public static class configParam{
        public static final String documentType = "documentType";
        public static final String incomeTransaction = "INCOME";
        public static final String incomeTransactionPdf = "RECIBO DE CAJA";
        public static final String expenseTransactionPdf = "COMPROBANTE DE EGRESO";
        public static final String expenseTransaction = "EXPENSE";
        public static final String expenseTransactionVar = "Salida";
        public static final String incomeTransactionVar = "Venta";
        public static final String statusTransactionPending = "PENDING";
        public static final String statusTransactionPartial = "PARTIAL";
        public static final String statusTransactionPaid = "PAID";
        public static final String statusOpen = "OPEN";
        public static final String statusClose = "CLOSED";
        public static final String thirdPartyRoleCustomer = "cliente";
        public static final String incomePrefix = "FE";
        public static final String expensePrefix = "FS";
    }

    public static class colors{
        public static final String primary = "primary";
        public static final String secondary = "secondary";
        public static final String success = "success";
        public static final String danger = "danger";
        public static final String warning = "warning";
        public static final String info = "info";
        public static final String light = "light";
        public static final String dark = "dark";

    }


    public static class utilLogs{
        public static final String separator = " ----------------------------------------------------------------- ";
    }

    public static class statuses {
        public static final String completed = "Completada";
        public static final String started = "Iniciada";
        public static final String cancelled = "Cancelada";
        public static final String suspended = "Suspendida";
        public static final String extended = "Extendida";
    }

    //TODO usar los codigos y manejar mensajes en el front
    public static class codeStatus{
        public static final String succeded = "AL00001";

    }
}
