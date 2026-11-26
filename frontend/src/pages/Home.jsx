import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMe } from "../services/authService";
import { removeToken } from "../utils/token";

function Home() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [tries, setTries] = useState(0);

  useEffect(() => {
    let active = true;

    getMe()
      .then((res) => {
        if (active) setUser(res.data);
      })
      .catch((err) => {
        if (!active) return;
        const status = err.response?.status;
        if (status === 401 || status === 403) {
          removeToken();
          navigate("/login");
        } else {
          setError("Could not load your details");
        }
      });

    return () => {
      active = false;
    };
  }, [navigate, tries]);

  const handleLogout = () => {
    removeToken();
    navigate("/login");
  };

  const handleRetry = () => {
    setError("");
    setTries(tries + 1);
  };

  if (error) {
    return (
      <div className="card">
        <p className="error">{error}</p>
        <button onClick={handleRetry}>Try again</button>
      </div>
    );
  }

  if (!user) {
    return <div className="card">Loading...</div>;
  }

  return (
    <div className="card">
      <h2>Welcome, {user.firstName || user.username}</h2>
      <p>Username: {user.username}</p>
      <p>Email: {user.email || "-"}</p>
      <p>Phone: {user.phone || "-"}</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
}

export default Home;
