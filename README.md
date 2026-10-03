# PetshopApp

## Variables de entorno

### `JWT_SECRET`

El backend (`app/`) firma los tokens JWT de autenticación con la clave definida en `JWT_SECRET`. Esta variable **debe configurarse en producción**.

Si no se define, se usa un valor por defecto hardcodeado en `app/src/main/resources/application.properties` que **solo es válido para desarrollo local** y no debe usarse en un entorno real, ya que cualquiera con acceso al código puede generar tokens válidos con ese secreto.
