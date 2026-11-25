import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMe } from "../services/authService";
import { removeToken } from "../utils/token";

function Home() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    getMe()
      .then((res) => setUser(res.data))
      .catch(() => {
        removeToken();
        navigate("/login");
      });
  }, [navigate]);

  const handleLogout = () => {
    removeToken();
    navigate("/login");
  };

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