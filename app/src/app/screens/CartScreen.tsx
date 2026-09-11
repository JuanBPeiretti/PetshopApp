import type { CartItem, Product } from "../types";

type Props = {
  items: CartItem[];
  onRemove: (item: CartItem) => void;
  onCheckout: () => void;
  onAddQty: (product: Product) => void;
};

const formatMoney = (value: number) =>
  new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" }).format(value);

export function CartScreen({ items, onRemove, onCheckout, onAddQty }: Props) {
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const shipping = items.length > 0 ? 1500 : 0;
  const total = subtotal + shipping;

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
                    <span>x{item.quantity}</span>
                  </div>
                </div>

                <div className="cart-actions">
                  <button className="secondary-btn" onClick={() => onAddQty({
                    id: item.productId,
                    name: item.name,
                    brand: "",
                    price: item.price,
                    rating: 0,
                    categoryId: "",
                    stock: 999,
                  })}>+1</button>
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
              <strong>{formatMoney(shipping)}</strong>
            </div>
            <div className="summary-row total-row">
              <span>Total</span>
              <strong>{formatMoney(total)}</strong>
            </div>
            <button className="primary-btn block" onClick={onCheckout}>Finalizar compra</button>
          </aside>
        </div>
      )}
    </div>
  );
}
