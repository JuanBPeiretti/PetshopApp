import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { fetchCategories, getGuestId, login, UNAUTHORIZED_EVENT } from "../api";

function mockFetchOnce(status: number, body: unknown) {
  return vi.fn().mockResolvedValue({
    ok: status >= 200 && status < 300,
    status,
    text: async () => JSON.stringify(body),
    json: async () => body,
  });
}

describe("api request wrapper", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("returns parsed JSON on a successful response", async () => {
    vi.stubGlobal("fetch", mockFetchOnce(200, [{ id: "alimentos", name: "Alimentos" }]));

    const categories = await fetchCategories();

    expect(categories).toEqual([{ id: "alimentos", name: "Alimentos" }]);
  });

  it("throws the backend's error message on a non-2xx response", async () => {
    vi.stubGlobal("fetch", mockFetchOnce(400, { error: "Producto no encontrado" }));

    await expect(fetchCategories()).rejects.toThrow("Producto no encontrado");
  });

  it("dispatches UNAUTHORIZED_EVENT on a 401 response", async () => {
    vi.stubGlobal("fetch", mockFetchOnce(401, { error: "No autorizado" }));
    const handler = vi.fn();
    window.addEventListener(UNAUTHORIZED_EVENT, handler);

    await expect(fetchCategories()).rejects.toThrow();

    expect(handler).toHaveBeenCalledTimes(1);
    window.removeEventListener(UNAUTHORIZED_EVENT, handler);
  });

  it("does not dispatch UNAUTHORIZED_EVENT for login (skipAuthRedirect)", async () => {
    vi.stubGlobal("fetch", mockFetchOnce(401, { error: "Credenciales inválidas" }));
    const handler = vi.fn();
    window.addEventListener(UNAUTHORIZED_EVENT, handler);

    await expect(login("a@a.com", "wrong")).rejects.toThrow("Credenciales inválidas");

    expect(handler).not.toHaveBeenCalled();
    window.removeEventListener(UNAUTHORIZED_EVENT, handler);
  });
});

describe("getGuestId", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("generates and persists a guest id on first call", () => {
    const id = getGuestId();

    expect(id).toBeTruthy();
    expect(localStorage.getItem("petshop_guest_id")).toBe(id);
  });

  it("returns the same id on subsequent calls", () => {
    const first = getGuestId();
    const second = getGuestId();

    expect(second).toBe(first);
  });
});
