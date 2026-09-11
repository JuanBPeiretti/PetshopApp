import React, { useState } from "react";

const API_BASE = "http://localhost:8080/api/auth";
const TOKEN_KEY = "petshop_token";
const USER_KEY = "petshop_user";

type AuthMode = "login" | "register";

export default function AuthScreen() {
  const [mode, setMode] = useState<AuthMode>("login");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("cliente@ejemplo.com");
  const [password, setPassword] = useState("password");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [user, setUser] = useState<any>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const endpoint = mode === "login" ? "/login" : "/register";
      const body =
        mode === "login"
          ? { email, password }
          : { name, email, password };

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data?.error || "Error al autenticar");
      }

      const token = data.token;
      const loggedUser = data.user;

      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(loggedUser));
      setUser(loggedUser);
      setError(null);
      alert(`Bienvenido, ${loggedUser.name || loggedUser.email}`);
    } catch (err: any) {
      setError(err.message || "No se pudo completar la autenticación");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}>
          <div style={styles.logo}>🐾</div>
          <h2 style={styles.title}>{mode === "login" ? "Iniciar sesión" : "Crear cuenta"}</h2>
          <p style={styles.subtitle}>Petshop App</p>
        </div>

        {user ? (
          <div style={styles.userBox}>
            <h3>Sesión activa</h3>
            <p><strong>Nombre:</strong> {user.name}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <button style={styles.primaryButton} onClick={handleLogout}>Cerrar sesión</button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} style={styles.form}>
            {mode === "register" && (
              <div style={styles.fieldWrap}>
                <label style={styles.label}>Nombre</label>
                <input
                  style={styles.input}
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Tu nombre"
                />
              </div>
            )}

            <div style={styles.fieldWrap}>
              <label style={styles.label}>Email</label>
              <input
                style={styles.input}
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="cliente@ejemplo.com"
              />
            </div>

            <div style={styles.fieldWrap}>
              <label style={styles.label}>Contraseña</label>
              <input
                style={styles.input}
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="********"
              />
            </div>

            {error && <div style={styles.error}>{error}</div>}

            <button type="submit" style={styles.primaryButton} disabled={loading}>
              {loading ? "Procesando..." : mode === "login" ? "Entrar" : "Registrarme"}
            </button>
          </form>
        )}

        <button
          type="button"
          onClick={() => {
            setMode((prev) => (prev === "login" ? "register" : "login"));
            setError(null);
          }}
          style={styles.switchButton}
        >
          {mode === "login" ? "Crear cuenta" : "Ya tengo cuenta"}
        </button>
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  page: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "linear-gradient(135deg, #fff7ed 0%, #f8fafc 100%)",
    padding: "24px",
    fontFamily: "Inter, Arial, sans-serif",
  },
  card: {
    width: "100%",
    maxWidth: "430px",
    background: "#ffffff",
    borderRadius: "22px",
    padding: "30px 24px",
    boxShadow: "0 18px 48px rgba(15, 23, 42, 0.08)",
    border: "1px solid #f1f5f9",
  },
  header: {
    textAlign: "center",
    marginBottom: "20px",
  },
  logo: {
    width: "72px",
    height: "72px",
    margin: "0 auto 12px",
    display: "grid",
    placeItems: "center",
    borderRadius: "20px",
    background: "linear-gradient(135deg, #f97316, #fb7185)",
    fontSize: "36px",
    color: "white",
    boxShadow: "0 12px 24px rgba(249, 115, 22, 0.25)",
  },
  title: {
    margin: 0,
    fontSize: "2rem",
    color: "#111827",
    fontWeight: 800,
  },
  subtitle: {
    margin: "8px 0 0",
    color: "#64748b",
    fontSize: "0.95rem",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "16px",
  },
  fieldWrap: {
    display: "flex",
    flexDirection: "column",
    gap: "8px",
  },
  label: {
    color: "#374151",
    fontWeight: 700,
    fontSize: "0.92rem",
  },
  input: {
    height: "46px",
    padding: "0 12px",
    borderRadius: "12px",
    border: "1px solid #e2e8f0",
    fontSize: "1rem",
    outline: "none",
    background: "#fff",
  },
  primaryButton: {
    width: "100%",
    height: "48px",
    border: "none",
    borderRadius: "12px",
    background: "linear-gradient(135deg, #f97316, #ef4444)",
    color: "#fff",
    fontWeight: 800,
    fontSize: "1rem",
    cursor: "pointer",
    boxShadow: "0 12px 24px rgba(249, 115, 22, 0.22)",
  },
  error: {
    background: "#fff1f2",
    border: "1px solid #fecdd3",
    color: "#be123c",
    padding: "10px 12px",
    borderRadius: "10px",
    fontWeight: 700,
    fontSize: "0.92rem",
  },
  switchButton: {
    marginTop: "18px",
    border: "none",
    background: "transparent",
    color: "#f97316",
    fontWeight: 800,
    width: "100%",
    cursor: "pointer",
  },
  userBox: {
    padding: "18px",
    background: "#f8fafc",
    borderRadius: "14px",
    border: "1px solid #e2e8f0",
    color: "#1f2937",
  },
};
