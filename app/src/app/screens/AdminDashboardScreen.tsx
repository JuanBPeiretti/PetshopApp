import { useEffect, useState } from "react";
import type { Category, OrderRecord, OrderStats, Product, ReturnRecord } from "../types";
import {
  createCategory,
  createProduct,
  deleteCategory,
  deleteProduct,
  fetchAllOrders,
  fetchAllReturns,
  fetchCategories,
  fetchOrderStats,
  fetchProducts,
  updateCategory,
  updateOrderStatus,
  updateProduct,
  updateReturnStatus,
  uploadImage,
} from "../api";

type Props = {
  authToken: string;
  categories: Category[];
};

type Tab = "stats" | "orders" | "products" | "categories" | "returns";

const EMPTY_CATEGORY_FORM = { id: "", name: "", color: "#f97316" };

const formatMoney = (value: number) =>
  new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" }).format(value);

const EMPTY_PRODUCT_FORM = {
  id: "",
  name: "",
  brand: "",
  price: "",
  oldPrice: "",
  rating: "",
  imageUrl: "",
  badge: "",
  categoryId: "",
  stock: "",
  precioPromocional: "",
  tipoPromocion: "",
};

export function AdminDashboardScreen({ authToken, categories }: Props) {
  const [tab, setTab] = useState<Tab>("stats");

  const [stats, setStats] = useState<OrderStats | null>(null);
  const [statsLoading, setStatsLoading] = useState(false);

  const [orders, setOrders] = useState<OrderRecord[]>([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [orderStatusFilter, setOrderStatusFilter] = useState("");

  const [products, setProducts] = useState<Product[]>([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [productForm, setProductForm] = useState(EMPTY_PRODUCT_FORM);
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [productError, setProductError] = useState<string | null>(null);
  const [imageUploading, setImageUploading] = useState(false);

  const [returns, setReturns] = useState<ReturnRecord[]>([]);
  const [returnsLoading, setReturnsLoading] = useState(false);

  const [categoryList, setCategoryList] = useState<Category[]>(categories);
  const [categoriesLoading, setCategoriesLoading] = useState(false);
  const [categoryForm, setCategoryForm] = useState(EMPTY_CATEGORY_FORM);
  const [editingCategoryId, setEditingCategoryId] = useState<string | null>(null);
  const [categoryError, setCategoryError] = useState<string | null>(null);

  const [actionError, setActionError] = useState<string | null>(null);

  const loadStats = async () => {
    setStatsLoading(true);
    try {
      const data = await fetchOrderStats(authToken);
      setStats(data);
    } catch (error) {
      console.error("No se pudieron cargar las estadísticas", error);
    } finally {
      setStatsLoading(false);
    }
  };

  const loadOrders = async () => {
    setOrdersLoading(true);
    try {
      const data = await fetchAllOrders(authToken);
      setOrders(data);
    } catch (error) {
      console.error("No se pudieron cargar las órdenes", error);
    } finally {
      setOrdersLoading(false);
    }
  };

  const loadProducts = async () => {
    setProductsLoading(true);
    try {
      const data = await fetchProducts();
      setProducts(data);
    } catch (error) {
      console.error("No se pudieron cargar los productos", error);
    } finally {
      setProductsLoading(false);
    }
  };

  const loadReturns = async () => {
    setReturnsLoading(true);
    try {
      const data = await fetchAllReturns(authToken);
      setReturns(data);
    } catch (error) {
      console.error("No se pudieron cargar las devoluciones", error);
    } finally {
      setReturnsLoading(false);
    }
  };

  const loadCategories = async () => {
    setCategoriesLoading(true);
    try {
      setCategoryList(await fetchCategories());
    } catch (error) {
      console.error("No se pudieron cargar las categorías", error);
    } finally {
      setCategoriesLoading(false);
    }
  };

  useEffect(() => {
    void loadStats();
    void loadOrders();
    void loadProducts();
    void loadReturns();
    void loadCategories();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleOrderStatusChange = async (order: OrderRecord, estado: string) => {
    setActionError(null);
    try {
      const updated = await updateOrderStatus(authToken, order.id, estado);
      setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
    } catch (error) {
      setActionError(error instanceof Error ? error.message : "No se pudo cambiar el estado de la orden");
    }
  };

  const filteredOrders = orderStatusFilter
    ? orders.filter((order) => order.estado === orderStatusFilter)
    : orders;

  const orderStatuses = Array.from(new Set(orders.map((o) => o.estado)));

  const startEditProduct = (product: Product) => {
    setEditingProductId(product.id);
    setProductForm({
      id: product.id,
      name: product.name,
      brand: product.brand,
      price: String(product.price),
      oldPrice: product.oldPrice != null ? String(product.oldPrice) : "",
      rating: String(product.rating),
      imageUrl: product.imageUrl || "",
      badge: product.badge || "",
      categoryId: product.categoryId,
      stock: String(product.stock),
      precioPromocional: product.precioPromocional != null ? String(product.precioPromocional) : "",
      tipoPromocion: product.tipoPromocion || "",
    });
    setProductError(null);
  };

  const resetProductForm = () => {
    setEditingProductId(null);
    setProductForm(EMPTY_PRODUCT_FORM);
    setProductError(null);
  };

  const handleProductSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setProductError(null);

    if (!productForm.name.trim() || !productForm.price || !productForm.categoryId) {
      setProductError("Completa al menos nombre, precio y categoría.");
      return;
    }

    const payload: Partial<Product> = {
      id: productForm.id.trim() || undefined,
      name: productForm.name.trim(),
      brand: productForm.brand.trim(),
      price: Number(productForm.price),
      oldPrice: productForm.oldPrice ? Number(productForm.oldPrice) : null,
      rating: productForm.rating ? Number(productForm.rating) : 0,
      imageUrl: productForm.imageUrl.trim(),
      badge: productForm.badge.trim() || undefined,
      categoryId: productForm.categoryId,
      stock: productForm.stock ? Number(productForm.stock) : 0,
      precioPromocional: productForm.precioPromocional ? Number(productForm.precioPromocional) : null,
      tipoPromocion: productForm.tipoPromocion.trim() || null,
    };

    try {
      if (editingProductId) {
        await updateProduct(authToken, editingProductId, payload);
      } else {
        await createProduct(authToken, payload);
      }
      resetProductForm();
      await loadProducts();
    } catch (error) {
      setProductError(error instanceof Error ? error.message : "No se pudo guardar el producto");
    }
  };

  const handleDeleteProduct = async (product: Product) => {
    setActionError(null);
    try {
      await deleteProduct(authToken, product.id);
      await loadProducts();
      if (editingProductId === product.id) {
        resetProductForm();
      }
    } catch (error) {
      setActionError(error instanceof Error ? error.message : "No se pudo eliminar el producto");
    }
  };

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setProductError(null);
    setImageUploading(true);
    try {
      const url = await uploadImage(authToken, file);
      setProductForm((prev) => ({ ...prev, imageUrl: url }));
    } catch (error) {
      setProductError(error instanceof Error ? error.message : "No se pudo subir la imagen");
    } finally {
      setImageUploading(false);
      e.target.value = "";
    }
  };

  const startEditCategory = (category: Category) => {
    setEditingCategoryId(category.id);
    setCategoryForm({ id: category.id, name: category.name, color: category.color || "#f97316" });
    setCategoryError(null);
  };

  const resetCategoryForm = () => {
    setEditingCategoryId(null);
    setCategoryForm(EMPTY_CATEGORY_FORM);
    setCategoryError(null);
  };

  const handleCategorySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setCategoryError(null);

    if (!categoryForm.name.trim()) {
      setCategoryError("Completa el nombre de la categoría.");
      return;
    }

    const payload: Partial<Category> = {
      id: categoryForm.id.trim() || undefined,
      name: categoryForm.name.trim(),
      color: categoryForm.color,
    };

    try {
      if (editingCategoryId) {
        await updateCategory(authToken, editingCategoryId, payload);
      } else {
        await createCategory(authToken, payload);
      }
      resetCategoryForm();
      await loadCategories();
    } catch (error) {
      setCategoryError(error instanceof Error ? error.message : "No se pudo guardar la categoría");
    }
  };

  const handleDeleteCategory = async (category: Category) => {
    setActionError(null);
    try {
      await deleteCategory(authToken, category.id);
      await loadCategories();
      if (editingCategoryId === category.id) {
        resetCategoryForm();
      }
    } catch (error) {
      setActionError(error instanceof Error ? error.message : "No se pudo eliminar la categoría");
    }
  };

  const handleReturnDecision = async (devolucion: ReturnRecord, estado: "APROBADA" | "RECHAZADA") => {
    setActionError(null);
    try {
      const updated = await updateReturnStatus(authToken, devolucion.id, estado);
      setReturns((prev) => prev.map((r) => (r.id === updated.id ? updated : r)));
    } catch (error) {
      setActionError(error instanceof Error ? error.message : "No se pudo actualizar la devolución");
    }
  };

  const pendingReturns = returns.filter((r) => r.estado === "PENDIENTE");

  return (
    <div className="page-shell admin-shell">
      <div className="section-header">
        <h2>Panel de administración</h2>
      </div>

      <div className="admin-tabs">
        <button className={tab === "stats" ? "admin-tab active" : "admin-tab"} onClick={() => setTab("stats")}>
          Estadísticas
        </button>
        <button className={tab === "orders" ? "admin-tab active" : "admin-tab"} onClick={() => setTab("orders")}>
          Órdenes
        </button>
        <button className={tab === "products" ? "admin-tab active" : "admin-tab"} onClick={() => setTab("products")}>
          Productos
        </button>
        <button className={tab === "categories" ? "admin-tab active" : "admin-tab"} onClick={() => setTab("categories")}>
          Categorías
        </button>
        <button className={tab === "returns" ? "admin-tab active" : "admin-tab"} onClick={() => setTab("returns")}>
          Devoluciones pendientes {pendingReturns.length > 0 ? `(${pendingReturns.length})` : ""}
        </button>
      </div>

      {actionError ? <div className="error-box">{actionError}</div> : null}

      {tab === "stats" ? (
        <section className="admin-section">
          {statsLoading || !stats ? (
            <div className="empty-state">Cargando estadísticas...</div>
          ) : (
            <>
              <div className="stats-grid">
                <div className="stat-card">
                  <span>Total de órdenes</span>
                  <strong>{stats.totalOrders}</strong>
                </div>
                <div className="stat-card">
                  <span>Ingresos totales</span>
                  <strong>{formatMoney(stats.totalRevenue)}</strong>
                </div>
                <div className="stat-card">
                  <span>Órdenes de hoy</span>
                  <strong>{stats.ordersToday}</strong>
                </div>
              </div>

              <div className="admin-table-wrap">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Producto</th>
                      <th>Unidades vendidas</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stats.topProducts.length === 0 ? (
                      <tr>
                        <td colSpan={3}>Todavía no hay ventas.</td>
                      </tr>
                    ) : (
                      stats.topProducts.map((product, index) => (
                        <tr key={product.productId}>
                          <td>{index + 1}</td>
                          <td>{product.name}</td>
                          <td>{product.totalQuantity}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </section>
      ) : null}

      {tab === "orders" ? (
        <section className="admin-section">
          <div className="toolbar-card">
            <div className="toolbar-row">
              <label>
                <span>Filtrar por estado</span>
                <select value={orderStatusFilter} onChange={(e) => setOrderStatusFilter(e.target.value)}>
                  <option value="">Todos</option>
                  {orderStatuses.map((estado) => (
                    <option key={estado} value={estado}>
                      {estado}
                    </option>
                  ))}
                </select>
              </label>
            </div>
          </div>

          {ordersLoading ? (
            <div className="empty-state">Cargando órdenes...</div>
          ) : filteredOrders.length === 0 ? (
            <div className="empty-state">No hay órdenes para mostrar.</div>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Fecha</th>
                    <th>Items</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredOrders.map((order) => (
                    <tr key={order.id}>
                      <td>{order.id}</td>
                      <td className="mono">{order.userId}</td>
                      <td>{new Date(order.fecha).toLocaleString("es-AR")}</td>
                      <td>{order.items.reduce((sum, i) => sum + i.quantity, 0)}</td>
                      <td>{formatMoney(order.total)}</td>
                      <td>
                        <span className="status-badge">{order.estado}</span>
                      </td>
                      <td>
                        <select
                          value=""
                          onChange={(e) => {
                            if (e.target.value) void handleOrderStatusChange(order, e.target.value);
                          }}
                        >
                          <option value="">Cambiar estado...</option>
                          <option value="PENDIENTE">PENDIENTE</option>
                          <option value="COMPLETADA">COMPLETADA</option>
                          <option value="ENVIADA">ENVIADA</option>
                          <option value="CANCELADA">CANCELADA</option>
                        </select>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      ) : null}

      {tab === "products" ? (
        <section className="admin-section">
          <form className="admin-form-grid" onSubmit={handleProductSubmit}>
            <label>
              <span>Nombre</span>
              <input value={productForm.name} onChange={(e) => setProductForm({ ...productForm, name: e.target.value })} />
            </label>
            <label>
              <span>Marca</span>
              <input value={productForm.brand} onChange={(e) => setProductForm({ ...productForm, brand: e.target.value })} />
            </label>
            <label>
              <span>Precio</span>
              <input type="number" value={productForm.price} onChange={(e) => setProductForm({ ...productForm, price: e.target.value })} />
            </label>
            <label>
              <span>Precio anterior</span>
              <input type="number" value={productForm.oldPrice} onChange={(e) => setProductForm({ ...productForm, oldPrice: e.target.value })} />
            </label>
            <label>
              <span>Rating</span>
              <input type="number" step="0.1" value={productForm.rating} onChange={(e) => setProductForm({ ...productForm, rating: e.target.value })} />
            </label>
            <label>
              <span>Stock</span>
              <input type="number" value={productForm.stock} onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })} />
            </label>
            <label>
              <span>Categoría</span>
              <select value={productForm.categoryId} onChange={(e) => setProductForm({ ...productForm, categoryId: e.target.value })}>
                <option value="">Seleccionar...</option>
                {categoryList.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              <span>Badge</span>
              <input value={productForm.badge} onChange={(e) => setProductForm({ ...productForm, badge: e.target.value })} />
            </label>
            <label>
              <span>Precio promocional</span>
              <input
                type="number"
                value={productForm.precioPromocional}
                onChange={(e) => setProductForm({ ...productForm, precioPromocional: e.target.value })}
              />
            </label>
            <label>
              <span>Tipo de promoción</span>
              <input
                placeholder="2x1, 20% OFF..."
                value={productForm.tipoPromocion}
                onChange={(e) => setProductForm({ ...productForm, tipoPromocion: e.target.value })}
              />
            </label>
            <label className="admin-form-wide">
              <span>URL de imagen</span>
              <input value={productForm.imageUrl} onChange={(e) => setProductForm({ ...productForm, imageUrl: e.target.value })} />
            </label>
            <label className="admin-form-wide">
              <span>Subir imagen</span>
              <input type="file" accept="image/png,image/jpeg,image/gif,image/webp" onChange={handleImageUpload} disabled={imageUploading} />
              {imageUploading ? <span className="upload-status">Subiendo...</span> : null}
              {productForm.imageUrl ? (
                <img src={productForm.imageUrl} alt="Vista previa" className="image-preview" />
              ) : null}
            </label>

            {productError ? <div className="error-box admin-form-wide">{productError}</div> : null}

            <div className="admin-form-actions admin-form-wide">
              <button className="primary-btn" type="submit">
                {editingProductId ? "Guardar cambios" : "Crear producto"}
              </button>
              {editingProductId ? (
                <button className="secondary-btn" type="button" onClick={resetProductForm}>
                  Cancelar edición
                </button>
              ) : null}
            </div>
          </form>

          {productsLoading ? (
            <div className="empty-state">Cargando productos...</div>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Marca</th>
                    <th>Precio</th>
                    <th>Promoción</th>
                    <th>Stock</th>
                    <th>Categoría</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((product) => (
                    <tr key={product.id}>
                      <td>{product.name}</td>
                      <td>{product.brand}</td>
                      <td>{formatMoney(product.price)}</td>
                      <td>
                        {product.precioPromocional != null
                          ? `${formatMoney(product.precioPromocional)} (${product.tipoPromocion || "s/tipo"})`
                          : "—"}
                      </td>
                      <td>{product.stock}</td>
                      <td>{product.categoryId}</td>
                      <td>
                        <div className="admin-row-actions">
                          <button className="secondary-btn" onClick={() => startEditProduct(product)}>
                            Editar
                          </button>
                          <button className="secondary-btn danger" onClick={() => handleDeleteProduct(product)}>
                            Eliminar
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      ) : null}

      {tab === "categories" ? (
        <section className="admin-section">
          <form className="admin-form-grid" onSubmit={handleCategorySubmit}>
            <label>
              <span>Nombre</span>
              <input value={categoryForm.name} onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })} />
            </label>
            <label>
              <span>Color</span>
              <input
                type="color"
                value={categoryForm.color}
                onChange={(e) => setCategoryForm({ ...categoryForm, color: e.target.value })}
              />
            </label>

            {categoryError ? <div className="error-box admin-form-wide">{categoryError}</div> : null}

            <div className="admin-form-actions admin-form-wide">
              <button className="primary-btn" type="submit">
                {editingCategoryId ? "Guardar cambios" : "Crear categoría"}
              </button>
              {editingCategoryId ? (
                <button className="secondary-btn" type="button" onClick={resetCategoryForm}>
                  Cancelar edición
                </button>
              ) : null}
            </div>
          </form>

          {categoriesLoading ? (
            <div className="empty-state">Cargando categorías...</div>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Color</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {categoryList.map((category) => (
                    <tr key={category.id}>
                      <td>{category.name}</td>
                      <td>
                        <span className="category-swatch" style={{ background: category.color || "#e2e8f0" }} />
                        {category.color}
                      </td>
                      <td>
                        <div className="admin-row-actions">
                          <button className="secondary-btn" onClick={() => startEditCategory(category)}>
                            Editar
                          </button>
                          <button className="secondary-btn danger" onClick={() => handleDeleteCategory(category)}>
                            Eliminar
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      ) : null}

      {tab === "returns" ? (
        <section className="admin-section">
          {returnsLoading ? (
            <div className="empty-state">Cargando devoluciones...</div>
          ) : pendingReturns.length === 0 ? (
            <div className="empty-state">No hay devoluciones pendientes.</div>
          ) : (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Motivo</th>
                    <th>Solicitada</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingReturns.map((devolucion) => (
                    <tr key={devolucion.id}>
                      <td>{devolucion.id}</td>
                      <td className="mono">{devolucion.userId}</td>
                      <td>{devolucion.productId}</td>
                      <td>{devolucion.cantidad}</td>
                      <td>{devolucion.motivo}</td>
                      <td>{new Date(devolucion.requestedAt).toLocaleString("es-AR")}</td>
                      <td>
                        <div className="admin-row-actions">
                          <button className="secondary-btn" onClick={() => handleReturnDecision(devolucion, "APROBADA")}>
                            Aprobar
                          </button>
                          <button className="secondary-btn danger" onClick={() => handleReturnDecision(devolucion, "RECHAZADA")}>
                            Rechazar
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      ) : null}
    </div>
  );
}
