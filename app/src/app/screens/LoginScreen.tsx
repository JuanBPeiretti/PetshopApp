import { useState } from "react";

type Props = {
  mode: "login" | "register";
  error: string | null;
  loading: boolean;
  onSubmit: (payload: { email: string; password: string; name?: string }) => void;
  onToggleMode: () => void;
};

export function LoginScreen({ mode, error, loading, onSubmit, onToggleMode }: Props) {
  const [email, setEmail] = useState("cliente@ejemplo.com");
  const [password, setPassword] = useState("password");
  const [name, setName] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ email, password, name: mode === "register" ? name : undefined });
  };

  return (
    <div className="page-shell auth-shell">
      <div className="auth-card">
        <div className="auth-header">
          <span className="eyebrow">Petshop</span>
          <h2>{mode === "login" ? "Ingresar" : "Crear cuenta"}</h2>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {mode === "register" ? (
            <label>
              <span>Nombre</span>
              <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Tu nombre" />
            </label>
          ) : null}

          <label>
            <span>Email</span>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="mail@ejemplo.com" />
          </label>

          <label>
            <span>Contraseña</span>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" />
          </label>

          {error ? <div className="error-box">{error}</div> : null}

          <button className="primary-btn block" type="submit" disabled={loading}>
            {loading ? "Procesando..." : mode === "login" ? "Iniciar sesión" : "Registrarme"}
          </button>
        </form>

        <button className="text-link center-link" onClick={onToggleMode}>
          {mode === "login" ? "Crear una cuenta" : "Ya tengo cuenta"}
        </button>
      </div>
    </div>
  );
}
