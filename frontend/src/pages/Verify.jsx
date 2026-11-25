import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { resendCode, verify } from "../services/authService";

function Verify() {
  const navigate = useNavigate();
  const location = useLocation();
  const [identifier, setIdentifier] = useState(location.state?.identifier || "");
  const [code, setCode] = useState("");
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [wait, setWait] = useState(0);

  useEffect(() => {
    if (wait <= 0) return;
    const timer = setTimeout(() => setWait(wait - 1), 1000);
    return () => clearTimeout(timer);
  }, [wait]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setInfo("");

    if (!identifier.trim()) {
      setError("Enter your username, email or phone");
      return;
    }
    if (!/^\d{5}$/.test(code)) {
      setError("The code must be 5 digits");
      return;
    }

    setLoading(true);
    try {
      await verify({ identifier: identifier.trim(), code });
      navigate("/login", { state: { verified: true } });
    } catch (err) {
      setError(err.response?.data?.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    setError("");
    setInfo("");

    if (!identifier.trim()) {
      setError("Enter your username, email or phone");
      return;
    }

    setResending(true);
    try {
      await resendCode({ identifier: identifier.trim() });
      setInfo("A new code has been sent");
      setWait(60);
    } catch (err) {
      setError(err.response?.data?.message || "Something went wrong");
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="card">
      <h2>Verify your account</h2>
      <p>Enter the 5-digit code.</p>
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Username, email or phone"
          value={identifier}
          onChange={(e) => setIdentifier(e.target.value)}
        />
        <input
          placeholder="5-digit code"
          maxLength={5}
          value={code}
          onChange={(e) => setCode(e.target.value)}
        />
        {error && <p className="error">{error}</p>}
        {info && <p className="success">{info}</p>}
        <button type="submit" disabled={loading}>
          {loading ? "Please wait..." : "Verify"}
        </button>
        <button
          type="button"
          className="secondary"
          onClick={handleResend}
          disabled={resending || wait > 0}
        >
          {wait > 0 ? `Resend code in ${wait}s` : "Resend code"}
        </button>
      </form>
      <p>
        Back to <Link to="/login">Login</Link> or <Link to="/register">Register</Link>
      </p>
    </div>
  );
}

export default Verify;