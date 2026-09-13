import type { Product } from "../types";

type Props = {
  products: Product[];
  onAddToCart: (product: Product) => void;
  onOpenProduct: (id: string) => void;
};

const formatMoney = (value: number) =>
  new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" }).format(value);

export function OffersScreen({ products, onAddToCart, onOpenProduct }: Props) {
  const offers = products.filter((product) => {
    const isDiscounted = Boolean(product.oldPrice && product.oldPrice > product.price);
    const hasPromoBadge = Boolean(product.badge && /(oferta|promo|descuento|sale)/i.test(product.badge));
    return isDiscounted || hasPromoBadge;
  });

  return (
    <div className="page-shell">
      <section className="offers-hero">
        <div>
          <span className="eyebrow">Ofertas exclusivas</span>
          <h1>Ahorrá en productos para toda la familia.</h1>
          <p>Promociones reales, descuentos por temporada y artículos seleccionados para tu mascota.</p>
        </div>
        <div className="offers-pill-wrap">
          <span className="offers-pill">Hasta 30% OFF</span>
        </div>
      </section>

      {offers.length === 0 ? (
        <div className="empty-state">Todavía no hay ofertas activas, pero pronto agregamos más promos.</div>
      ) : (
        <div className="product-grid">
          {offers.map((product) => (
            <article key={product.id} className="product-card">
              <div className="product-image-wrap product-clickable" onClick={() => onOpenProduct(product.id)}>
                <img
                  src={product.imageUrl || "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=700&q=80"}
                  alt={product.name}
                />
                {product.badge ? <span className="product-badge">{product.badge}</span> : null}
              </div>
              <div className="product-body">
                <span className="brand">{product.brand}</span>
                <h3 onClick={() => onOpenProduct(product.id)} className="product-name-link">{product.name}</h3>
                <div className="rating-row">
                  <span>⭐ {product.rating.toFixed(1)}</span>
                  <span>{product.stock} disponibles</span>
                </div>
                <div className="price-row">
                  <strong>{formatMoney(product.price)}</strong>
                  {product.oldPrice ? <span>{formatMoney(product.oldPrice)}</span> : null}
                </div>
                <button className="primary-btn block" onClick={() => onAddToCart(product)}>
                  Agregar al carrito
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
