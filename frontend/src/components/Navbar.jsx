import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <nav className="navbar">
      <div className="container navbar-inner">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">🚗</span>
          AutoVault
        </Link>
        <div className="navbar-actions">
          {isAuthenticated ? (
            <>
              <span className="navbar-user">
                {user.name}
                <span className={`badge badge-${user.role === 'ADMIN' ? 'admin' : 'user'}`}>
                  {user.role}
                </span>
              </span>
              <button className="btn btn-secondary" onClick={logout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-secondary">Login</Link>
              <Link to="/register" className="btn">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
