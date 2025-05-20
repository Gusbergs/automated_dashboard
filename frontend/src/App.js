import './App.css';
import Dashboard from './Dashboard';
import { useEffect, useState } from 'react';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true); // Visar laddning innan beslut

  useEffect(() => {
    fetch('http://localhost:8080/api/user', {
      method: 'GET',
      credentials: 'include', // viktigt för att cookies/token ska skickas
    })
      .then(res => {
        if (res.status === 401) {
          // Användaren är inte inloggad → visa login-knapp istället för redirecta
          setUser(null);
          setLoading(false);
          return null;
        } else if (!res.ok) {
          throw new Error('Serverfel vid autentisering');
        }
        return res.json();
      })
      .then(data => {
        if (data) setUser(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Fel vid hämtning av användare:', err);
        setUser(null);
        setLoading(false);
      });
  }, []);

  const handleLogin = () => {
    // Direkt redirect till Spring Security Google-login
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  if (loading) return <div>Laddar...</div>;

  return (
    <div className="App">
      <header style={{ padding: '1rem' }}>
        {user ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <img src={user.picture} alt="profil" style={{ width: 40, borderRadius: '50%' }} />
            <div>
              <strong>{user.name}</strong><br />
              <small>{user.email}</small>
            </div>
            <a href="http://localhost:8080/logout">Logga ut</a>
          </div>
        ) : (
          <button onClick={handleLogin}>Logga in med Google</button>
        )}
      </header>

      <Dashboard user={user} />
    </div>
  );
}

export default App;
