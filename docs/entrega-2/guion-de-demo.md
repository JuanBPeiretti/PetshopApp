# Guion de la 2.ª Entrega Parcial (16/11): núcleo funcionando en vivo

Cada punto de la consigna, qué mostrar y dónde está en el código. Todo se muestra funcionando, no con capturas.

## Antes de empezar (5 minutos antes del turno)
```bash
docker compose up --build        # desde la raíz; la primera vez tarda unos minutos
```
- Tienda: <http://localhost:5173> · API: <http://localhost:8080/api> · Swagger: <http://localhost:8080/swagger-ui.html>
- Usuarios de desarrollo: `admin@petshop.com` / `admin123` (administrador) y `cliente@ejemplo.com` / `password` (cliente).
- Tener abiertas dos terminales: una con `docker compose logs -f backend` y otra para los `curl`.

## 1. Componentes en capas y primer flujo de punta a punta (3 min)
- Mostrar [ADR 0001](../adr/0001-arquitectura-en-capas.md): `controller → service → repository → model`.
- En la tienda: ingresar como cliente, agregar productos al carrito, elegir envío por código postal y **pagar con la tarjeta de prueba aprobada**.
  Resultado: pantalla «Compra confirmada».
- En la terminal de logs aparece el evento de la venta en la cola (`[COLA ventas.confirmadas] VentaConfirmada ventaId=...`): eso es la **mensajería funcionando**.

## 2. Patrón State con 409 ante una transición inválida (3 min) — [ADR 0003](../adr/0003-patron-state-en-la-venta.md)
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@petshop.com","password":"admin123"}' | jq -r .token)
ID=$(curl -s localhost:8080/api/orders -H "X-Auth-Token: $TOKEN" | jq '[.[]|select(.estado=="COMPLETADA")][0].id')

# COMPLETADA -> ENVIADA: permitido
curl -i -X PATCH localhost:8080/api/v1/ventas/$ID -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/json' -d '{"estado":"ENVIADA"}'
# ENVIADA -> ENVIADA y ENVIADA -> CANCELADA: 409 Conflict (Problem Details)
curl -i -X PATCH localhost:8080/api/v1/ventas/$ID -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/json' -d '{"estado":"ENVIADA"}'
curl -i -X PATCH localhost:8080/api/v1/ventas/$ID -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/json' -d '{"estado":"CANCELADA"}'
```
Se espera: `200`, luego `409` con `Content-Type: application/problem+json` y `type: urn:petshop:problem:transicion-invalida`.
- En el panel de administrador, el desplegable de un pedido enviado o cancelado dice «Sin cambios posibles».
- Código: `model/EstadoVenta.java` (la tabla de transiciones) y `EstadoVentaTest` (12 combinaciones: 4 permitidas y 8 rechazadas).

## 3. Seguridad base: JWT y control de acceso por rol (2 min) — [ADR 0011](../adr/0011-jwt-y-control-de-acceso-por-rol.md)
```bash
curl -i localhost:8080/api/orders                                                          # 401: sin sesión
CLI=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"email":"cliente@ejemplo.com","password":"password"}' | jq -r .token)
curl -i localhost:8080/api/orders -H "X-Auth-Token: $CLI"                                  # 403: es cliente, no administrador
curl -i localhost:8080/api/orders -H "X-Auth-Token: $TOKEN"                                # 200: administrador
```

## 4. Patrones implementados, cada uno con su ADR (2 min)
| Patrón | Dónde está | ADR |
| --- | --- | --- |
| State | `model/EstadoVenta` | [0003](../adr/0003-patron-state-en-la-venta.md) |
| Strategy | `service/ProductSortStrategy` (`PriceAscStrategy`, `PriceDescStrategy`); en vivo: `GET /api/products?sort=menorprecio` y `?sort=mayorprecio` | [0004](../adr/0004-patron-strategy-en-el-orden-de-productos.md) |
| Adapter | `payment/PaymentGateway` → `SoapPaymentGateway`; `MercadoPagoClient` → `HttpMercadoPagoClient` | [0005](../adr/0005-patron-adapter-en-las-pasarelas-de-pago.md) |
| Outbox | `service/OutboxRelay`, tabla `outbox_events` | [0006](../adr/0006-outbox-para-los-eventos-de-venta.md) |
| Consumidor idempotente + DLQ | `service/VentaEventListener`, `DlqService` | [0007](../adr/0007-consumidor-idempotente-y-dlq.md) |

## 5. Primeros tests unitarios (1 min)
```bash
cd app && ./mvnw test          # 460+ pruebas; la cobertura queda en target/site/jacoco/index.html
```
El CI los corre en cada push (GitHub Actions) y el resultado está en el Pull Request.

## Lo que falta confirmar fuera del código
- **Jira al día:** historias de usuario con su estado real, tareas de esta entrega y defectos (el [registro](../testing/registro-de-defectos.md) tiene los 17 con causa y cierre para cargarlos).
- **Feedback del diseño incorporado:** llevar la lista de observaciones del 03/11 y señalar dónde se corrigió cada una.
- Llegar con el sistema ya levantado: el primer `docker compose up --build` compila todo y tarda varios minutos.
