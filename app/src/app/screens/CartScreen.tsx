import { useState } from "react";
import type { CartItem, ShippingInfo } from "../types";

type Props = {
  items: CartItem[];
  requireShipping: boolean;
  onRemove: (item: CartItem) => void;
  onCheckout: (shipping: Partial<ShippingInfo>) => void;
  onIncrement: (item: CartItem) => void;
  onDecrement: (item: CartItem) => void;
};

const formatMoney = (value: number) =>
  new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" }).format(value);

const EMPTY_SHIPPING: ShippingInfo = {
  nombre: "",
  direccion: "",
  ciudad: "",
  codigoPostal: "",
  telefono: "",
};

export function CartScreen({ items, requireShipping, onRemove, onCheckout, onIncrement, onDecrement }: Props) {
  const [shipping, setShipping] = useState<ShippingInfo>(EMPTY_SHIPPING);

  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const shippingCost = items.length > 0 ? 1500 : 0;
  const total = subtotal + shippingCost;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onCheckout(requireShipping ? shipping : {});
  };

  return (
    <div className="page-shell cart-shell">
      <div className="section-header">
        <h2>Carrito</h2>
      </div>

      {items.length === 0 ? (
        <div className="empty-state">Tu carrito está vacío.</div>
      ) : (
        <div className="cart-layout">
          <div className="cart-list">
            {items.map((item) => (
              <div key={`${item.productId}-${item.variant}`} className="cart-item-card">
                <div className="cart-thumb">
                  <img
                    src="https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=300&q=80"
                    alt={item.name}
                  />
                </div>

                <div className="cart-details">
                  <h3>{item.name}</h3>
                  <p>{item.variant}</p>
                  <div className="cart-meta-row">
                    <strong>{formatMoney(item.price)}</strong>
                    <div className="qty-stepper">
                      <button className="secondary-btn" onClick={() => onDecrement(item)} aria-label="Restar cantidad">−</button>
                      <span>{item.quantity}</span>
                      <button className="secondary-btn" onClick={() => onIncrement(item)} aria-label="Sumar cantidad">+</button>
                    </div>
                  </div>
                </div>

                <div className="cart-actions">
                  <button className="secondary-btn danger" onClick={() => onRemove(item)}>Quitar</button>
                </div>
              </div>
            ))}
          </div>

          <aside className="cart-summary">
            <h3>Resumen</h3>
            <div className="summary-row">
              <span>Subtotal</span>
              <strong>{formatMoney(subtotal)}</strong>
            </div>
            <div className="summary-row">
              <span>Envío</span>
              <strong>{formatMoney(shippingCost)}</strong>
            </div>
            <div className="summary-row total-row">
              <span>Total</span>
              <strong>{formatMoney(total)}</strong>
            </div>

            <form onSubmit={handleSubmit} className="shipping-form">
              {requireShipping ? (
                <>
                  <h4>Datos de envío</h4>
                  <label>
                    <span>Nombre y apellido</span>
                    <input
                      required
                      value={shipping.nombre}
                      onChange={(e) => setShipping({ ...shipping, nombre: e.target.value })}
                    />
                  </label>
                  <label>
                    <span>Dirección</span>
                    <input
                      required
                      value={shipping.direccion}
                      onChange={(e) => setShipping({ ...shipping, direccion: e.target.value })}
                    />
                  </label>
                  <div className="shipping-form-row">
                    <label>
                      <span>Ciudad</span>
                      <input
                        required
                        value={shipping.ciudad}
                        onChange={(e) => setShipping({ ...shipping, ciudad: e.target.value })}
                      />
                    </label>
                    <label>
                      <span>Código postal</span>
                      <input
                        value={shipping.codigoPostal}
                        onChange={(e) => setShipping({ ...shipping, codigoPostal: e.target.value })}
                      />
                    </label>
                  </div>
                  <label>
                    <span>Teléfono (opcional)</span>
                    <input
                      value={shipping.telefono}
                      onChange={(e) => setShipping({ ...shipping, telefono: e.target.value })}
                    />
                  </label>
                </>
              ) : (
                <p className="review-login-hint">Iniciá sesión para guardar tu pedido y ver el historial.</p>
              )}
              <button className="primary-btn block" type="submit">Finalizar compra</button>
            </form>
          </aside>
        </div>
      )}
    </div>
  );
}
