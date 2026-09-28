import type { CartItem, Category, OrderRecord, OrderStats, Product, ReturnRecord, Review, User } from "./types";

const API_BASE_URL = "http://localhost:8080/api";
export const AUTH_TOKEN_KEY = "petshop_auth_token";
export const AUTH_USER_KEY = "petshop_auth_user";
export const GUEST_ID_KEY = "petshop_guest_id";

async function request<T>(input: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers ?? {});
  if (!headers.has("Content-Type") && !(init?.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${API_BASE_URL}${input}`, {
    ...init,
    headers,
  });

  if (!response.ok) {
    const text = await response.text();
    let message = text || "Request failed";
    try {
      const parsed = JSON.parse(text) as { error?: string };
      if (parsed.error) message = parsed.error;
    } catch {
      // not JSON, keep raw text
    }
    throw new Error(message);
  }

  return (await response.json()) as T;
}

export function getGuestId(): string {
  try {
    let id = localStorage.getItem(GUEST_ID_KEY);
    if (!id) {
      id = crypto.randomUUID();
      localStorage.setItem(GUEST_ID_KEY, id);
    }
    return id;
  } catch {
    return "unknown";
  }
}

export function getAuthToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

export function getCurrentUser(): User | null {
  const raw = localStorage.getItem(AUTH_USER_KEY);
  return raw ? (JSON.parse(raw) as User) : null;
}

export async function fetchCategories(): Promise<Category[]> {
  return request<Category[]>("/categories");
}

export async function createCategory(token: string, category: Partial<Category>): Promise<Category> {
  return request<Category>("/categories", {
    method: "POST",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify(category),
  });
}

export async function updateCategory(token: string, id: string, category: Partial<Category>): Promise<Category> {
  return request<Category>(`/categories/${id}`, {
    method: "PUT",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify(category),
  });
}

export async function deleteCategory(token: string, id: string): Promise<{ ok: boolean }> {
  return request<{ ok: boolean }>(`/categories/${id}`, {
    method: "DELETE",
    headers: { "X-Auth-Token": token },
  });
}

export async function fetchProducts(category?: string, sort?: string): Promise<Product[]> {
  const params = new URLSearchParams();
  if (category && category !== "all") params.set("category", category);
  if (sort) params.set("sort", sort);
  const query = params.toString();
  return request<Product[]>(`/products${query ? `?${query}` : ""}`);
}

export async function fetchProduct(id: string): Promise<Product> {
  return request<Product>(`/products/${id}`);
}

export async function fetchProductReviews(productId: string): Promise<Review[]> {
  return request<Review[]>(`/products/${productId}/reviews`);
}

export async function submitReview(token: string, productId: string, rating: number, comment: string): Promise<Review> {
  return request<Review>(`/products/${productId}/reviews`, {
    method: "POST",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ rating, comment }),
  });
}

export async function login(email: string, password: string): Promise<{ token: string; user: User }> {
  return request<{ token: string; user: User }>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function register(email: string, password: string, name: string): Promise<{ token: string; user: User }> {
  return request<{ token: string; user: User }>("/auth/register", {
    method: "POST",
    body: JSON.stringify({ email, password, name }),
  });
}

export async function recoverPassword(email: string): Promise<{ resetToken: string; expiresInMinutes: number }> {
  return request<{ resetToken: string; expiresInMinutes: number }>("/auth/recover", {
    method: "POST",
    body: JSON.stringify({ email }),
  });
}

export async function resetPassword(token: string, password: string): Promise<{ ok: boolean }> {
  return request<{ ok: boolean }>("/auth/reset", {
    method: "POST",
    body: JSON.stringify({ token, password }),
  });
}

export async function fetchCurrentUser(token: string): Promise<User> {
  return request<User>("/auth/me", {
    headers: {
      "X-Auth-Token": token,
    },
  });
}

export async function updateMyProfile(token: string, name: string): Promise<User> {
  return request<User>("/auth/me", {
    method: "PUT",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ name }),
  });
}

export async function changePassword(token: string, currentPassword: string, newPassword: string): Promise<{ ok: boolean }> {
  return request<{ ok: boolean }>("/auth/change-password", {
    method: "POST",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ currentPassword, newPassword }),
  });
}

function cartHeaders(token?: string | null): Record<string, string> {
  const headers: Record<string, string> = { "X-Guest-Id": getGuestId() };
  if (token) headers["X-Auth-Token"] = token;
  return headers;
}

export async function fetchCart(token?: string | null): Promise<CartItem[]> {
  return request<CartItem[]>("/cart", {
    headers: cartHeaders(token),
  });
}

export async function addToCart(token: string | null, item: CartItem): Promise<CartItem[]> {
  return request<CartItem[]>("/cart/add", {
    method: "POST",
    headers: cartHeaders(token),
    body: JSON.stringify(item),
  });
}

export async function removeFromCart(token: string | null, item: CartItem): Promise<CartItem[]> {
  return request<CartItem[]>("/cart/remove", {
    method: "POST",
    headers: cartHeaders(token),
    body: JSON.stringify(item),
  });
}

export async function incrementCartItem(token: string | null, productId: string): Promise<CartItem[]> {
  return request<CartItem[]>(`/cart/items/${productId}/increment`, {
    method: "PUT",
    headers: cartHeaders(token),
  });
}

export async function decrementCartItem(token: string | null, productId: string): Promise<CartItem[]> {
  return request<CartItem[]>(`/cart/items/${productId}/decrement`, {
    method: "PUT",
    headers: cartHeaders(token),
  });
}

export async function checkoutCart(token?: string | null): Promise<{ ok: boolean; items: CartItem[] }> {
  return request<{ ok: boolean; items: CartItem[] }>("/cart/checkout", {
    method: "POST",
    headers: cartHeaders(token),
  });
}

export async function fetchAllOrders(token: string): Promise<OrderRecord[]> {
  return request<OrderRecord[]>("/orders", {
    headers: { "X-Auth-Token": token },
  });
}

export async function fetchMyOrders(token: string): Promise<OrderRecord[]> {
  return request<OrderRecord[]>("/orders/me", {
    headers: { "X-Auth-Token": token },
  });
}

export async function fetchOrderStats(token: string): Promise<OrderStats> {
  return request<OrderStats>("/orders/stats", {
    headers: { "X-Auth-Token": token },
  });
}

export async function updateOrderStatus(token: string, orderId: number, estado: string): Promise<OrderRecord> {
  return request<OrderRecord>(`/orders/${orderId}/status`, {
    method: "PUT",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ estado }),
  });
}

export async function createProduct(token: string, product: Partial<Product>): Promise<Product> {
  return request<Product>("/products", {
    method: "POST",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify(product),
  });
}

export async function updateProduct(token: string, id: string, product: Partial<Product>): Promise<Product> {
  return request<Product>(`/products/${id}`, {
    method: "PUT",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify(product),
  });
}

export async function deleteProduct(token: string, id: string): Promise<{ ok: boolean }> {
  return request<{ ok: boolean }>(`/products/${id}`, {
    method: "DELETE",
    headers: { "X-Auth-Token": token },
  });
}

export async function fetchAllReturns(token: string): Promise<ReturnRecord[]> {
  return request<ReturnRecord[]>("/returns", {
    headers: { "X-Auth-Token": token },
  });
}

export async function fetchMyReturns(token: string): Promise<ReturnRecord[]> {
  return request<ReturnRecord[]>("/returns/me", {
    headers: { "X-Auth-Token": token },
  });
}

export async function requestReturn(
  token: string,
  productId: string,
  cantidad: number,
  motivo: string,
): Promise<ReturnRecord> {
  return request<ReturnRecord>("/returns", {
    method: "POST",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ productId, cantidad, motivo }),
  });
}

export async function updateReturnStatus(
  token: string,
  returnId: number,
  estado: "APROBADA" | "RECHAZADA",
): Promise<ReturnRecord> {
  return request<ReturnRecord>(`/returns/${returnId}/status`, {
    method: "PATCH",
    headers: { "X-Auth-Token": token },
    body: JSON.stringify({ estado }),
  });
}
