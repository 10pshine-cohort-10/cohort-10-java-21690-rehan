import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import { saveToken } from "../utils/token";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ identifier: "", password: "" });
  const [error, setError] = useState("");
  const [needsVerify, setNeedsVerify] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setNeedsVerify(false);

    if (!form.identifier.trim() || !form.password) {
      setError("Enter your username, email or phone and your password");
      return;
    }

    setLoading(true);
    try {
      const res = await login({ identifier: form.identifier.trim(), password: form.password });
      saveToken(res.data.token);
      navigate("/home");
    } catch (err) {
      setError(err.response?.data?.message || "Something went wrong");
      if (err.response?.status === 403) {
        setNeedsVerify(true);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Login</h2>
      {location.state?.verified && (
        <p className="success">Account verified. You can log in now.</p>
      )}
      <form onSubmit={handleSubmit}>
        <input
          name="identifier"
          placeholder="Username, email or phone"
          value={form.identifier}
          onChange={handleChange}
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
        />
        {error && <p className="error">{error}</p>}
        {needsVerify && (
          <p>
            <Link to="/verify" state={{ identifier: form.identifier.trim() }}>
              Verify now
            </Link>
          </p>
        )}
        <button type="submit" disabled={loading}>
          {loading ? "Please wait..." : "Login"}
        </button>
      </form>
      <p>
        No account? <Link to="/register">Register</Link>
      </p>
    </div>
  );
}

export default Login;