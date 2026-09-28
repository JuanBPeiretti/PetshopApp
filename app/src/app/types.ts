export type Category = {
  id: string;
  name: string;
  color?: string;
};

export type Product = {
  id: string;
  name: string;
  brand: string;
  price: number;
  oldPrice?: number | null;
  rating: number;
  imageUrl?: string;
  badge?: string;
  categoryId: string;
  stock: number;
  precioPromocional?: number | null;
  tipoPromocion?: string | null;
  hasVariants?: boolean;
};

export type ProductVariant = {
  id: number;
  productId: string;
  talle?: string | null;
  color?: string | null;
  stock: number;
};

export type CartItem = {
  productId: string;
  name: string;
  variant: string;
  variantId?: number | null;
  quantity: number;
  price: number;
};

export type User = {
  id: string;
  email: string;
  name: string;
  password?: string;
  role?: "CUSTOMER" | "ADMIN";
};

export type OrderItem = {
  productId: string;
  quantity: number;
  price: number;
  variant?: string | null;
};

export type OrderRecord = {
  id: number;
  userId: string;
  fecha: string;
  items: OrderItem[];
  total: number;
  estado: string;
};

export type TopProductStat = {
  productId: string;
  name: string;
  totalQuantity: number;
};

export type OrderStats = {
  totalOrders: number;
  totalRevenue: number;
  ordersToday: number;
  topProducts: TopProductStat[];
};

export type ReturnRecord = {
  id: number;
  userId: string;
  productId: string;
  cantidad: number;
  motivo: string;
  estado: "PENDIENTE" | "APROBADA" | "RECHAZADA" | "PROCESADO";
  requestedAt: string;
};

export type Review = {
  id: number;
  productId: string;
  authorName: string;
  rating: number;
  comment: string;
  createdAt: string;
};

export type ShippingInfo = {
  nombre: string;
  direccion: string;
  ciudad: string;
  codigoPostal: string;
  telefono: string;
};

export type CheckoutResult = {
  ok: boolean;
  items: CartItem[];
  subtotal: number;
  shippingCost: number;
  total: number;
  orderId: number | null;
};

export type View =
  | "home"
  | "products"
  | "offers"
  | "categories"
  | "cart"
  | "login"
  | "admin"
  | "orders"
  | "account"
  | "confirmation"
  | "terms"
  | "privacy"
  | "contact";
