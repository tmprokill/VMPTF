import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="header-logo">
          Dev<span>Blog</span>
        </Link>
        <nav className="header-nav">
          {user ? (
            <>
              <span className="header-user">{user.username}</span>
              <Link to="/create" className="btn btn-primary btn-sm">
                + Нова стаття
              </Link>
              <button onClick={handleLogout} className="btn btn-ghost btn-sm">
                Вийти
              </button>
            </>
          ) : (
            <>``
              <Link to="/login" className="btn btn-ghost btn-sm">Увійти</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Реєстрація</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
