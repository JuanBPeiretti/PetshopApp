import type { CartItem, Category, Product, User } from "./types";

const API_BASE_URL = "http://localhost:8080/api";
export const AUTH_TOKEN_KEY = "petshop_auth_token";
export const AUTH_USER_KEY = "petshop_auth_user";

async function request<T>(input: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${input}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Request failed");
  }

  return (await response.json()) as T;
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

export async function fetchCurrentUser(token: string): Promise<User> {
  return request<User>("/auth/me", {
    headers: {
      "X-Auth-Token": token,
    },
  });
}

export async function fetchCart(token?: string | null): Promise<CartItem[]> {
  const headers: Record<string, string> = {};
  if (token) headers["X-Auth-Token"] = token;
  return request<CartItem[]>("/cart", {
    headers,
  });
}

export async function addToCart(token: string | null, item: CartItem): Promise<CartItem[]> {
  const headers: Record<string, string> = {};
  if (token) headers["X-Auth-Token"] = token;
  return request<CartItem[]>("/cart/add", {
    method: "POST",
    headers,
    body: JSON.stringify(item),
  });
}

export async function removeFromCart(token: string | null, item: CartItem): Promise<CartItem[]> {
  const headers: Record<string, string> = {};
  if (token) headers["X-Auth-Token"] = token;
  return request<CartItem[]>("/cart/remove", {
    method: "POST",
    headers,
    body: JSON.stringify(item),
  });
}
