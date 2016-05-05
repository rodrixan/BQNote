# Notas de desarrollo

## OCR no implementado
En un principio se iba a usar OCR para pasar las notas hechas a mano a texto de ordenador, pero la infructuosa búsqueda de SDKs gratuitos no lo permitió. Se buscaron varias alternativas (API de OCR online, OCR integrado en la app que resultó no soportar escritura a mano, integrar otra aplicación...), ninguna de ellas resultaron como deberían.

Esta solución me pareció la adecuada dado que el propio usuario es capaz de guardar la nota tal cual la pensó. Cierto es que es en cierto modo "poco usable" en el sentido de que de este modo no se orienta tanto a una nota "escrita".

## Tablet
Por otro lado se ha implementado el layout para tablet: muestra en el lado ixquierdo la lista de notas y en el derecho la nota seleccionada. Al crear nueva nota en modo "a mano", se usa toda la pantalla disponible.

## Mejoras potenciales
Si bien es cierto que es una aplicación sencilla, en un futuro podría llegar a usarse de manera real. Esto me lleva a pensar en algunas mejoras posibles para el mejor rendimiento y usabilidad de la aplicación:

+ Más filtros
+ Animaciones para feedback
+ Reconocimiento con cámara para documentos
+ Escritura a mano con selector de colores y tipos de trazos
+ ...

### Datos del desarrollador

Para cualquier duda o sugerencia, mis datos de contacto son:
_Rodrigo de Blas García_
_rodri.xan@gmail.com_
