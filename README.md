Microservicio de Productos

Este proyecto corresponde a un microservicio desarrollado en Java con Spring Boot, el cual permite gestionar productos, usuarios y órdenes.

El objetivo de este trabajo fue aplicar conceptos de control de versiones y prácticas DevOps utilizando GitHub, GitHub Actions, Docker y herramientas de calidad de software.

  Para este proyecto se utilizó el modelo GitFlow.
  
  Las ramas utilizadas fueron:

  * main: contiene la versión estable del proyecto.
  * develop: rama donde se integran los cambios en desarrollo.
  * feature/: utilizada para implementar nuevas funcionalidades.
  * hotfix/: utilizada para corregir errores importantes.

Decidí usar GitFlow porque permite organizar mejor el trabajo, separar los cambios y evitar errores en la versión principal.

El flujo que se siguió fue el siguiente:

1. Se creó la rama develop desde main.
2. Se crearon ramas feature desde develop para agregar nuevas funcionalidades.
3. Cada cambio se subió mediante commits y luego se hizo un Pull Request hacia develop.
4. Para corregir un error, se creó una rama hotfix desde main.
5. El hotfix fue integrado nuevamente a main mediante un Pull Request.
6. Durante el proceso se resolvieron conflictos de código manualmente.


  Nombres de ramas:

* feature/agregar-endpoint
* feature/crear-producto
* hotfix/error-productos

  Mensajes de commit

* feat: para nuevas funcionalidades.
* fix: para correcciones de errores.
* docs: para documentación.
* ci: para cambios relacionados con integración continua.

Durante el desarrollo se realizaron:

- 2 ramas feature para simular nuevas funcionalidades.
- 1 rama hotfix para corregir un error.
- Uso de Pull Requests para integrar cambios.
- Resolución de conflictos en el código.
- Implementación de pruebas unitarias con JUnit y Mockito.
- Generación de reportes de cobertura utilizando JaCoCo.
- Configuración de Docker para contenedorización del microservicio.
- Configuración de Docker Compose para facilitar la ejecución del proyecto.
- Automatización de tareas mediante GitHub Actions.

Esto permitió simular un trabajo colaborativo y aplicar prácticas básicas de DevOps.

  GitHub Actions
  Se configuró un flujo básico de integración continua que se ejecuta automáticamente cuando:
  
  -Se realiza un push a la rama main.
  -Se crea o actualiza un Pull Request.

  El pipeline realiza las siguientes tareas:
  
  -Compilación del proyecto.
  -Ejecución de pruebas unitarias.
  -Generación de cobertura de código con JaCoCo.
  -Revisión de dependencias.
  -Empaquetado de la aplicación.
  
  Esto ayuda a verificar que los cambios se integren correctamente antes de ser publicados.

Cobertura de pruebas
  Se utilizó JaCoCo para generar reportes de cobertura de código a partir de las pruebas unitarias realizadas.
  La cobertura se genera automáticamente tanto de forma local como dentro del pipeline de GitHub Actions.

 Docker
  El proyecto fue preparado para ejecutarse mediante contenedores Docker.
  Además, se incorporó Docker Compose para simplificar la ejecución de los servicios necesarios para el proyecto.

Conclusión
Este trabajo me permitió entender mejor cómo funciona Git en un entorno colaborativo, el uso de ramas, 
la importancia de los Pull Requests y la automatización de procesos mediante herramientas DevOps.
También permitió aplicar conceptos de integración continua, pruebas automatizadas, 
cobertura de código y contenedorización de aplicaciones.
