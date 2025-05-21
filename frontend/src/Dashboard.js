import React, { useEffect, useState, useMemo } from 'react';
import {
  WiDaySunny,
  WiDaySunnyOvercast,
  WiDayCloudy,
  WiCloud,
  WiCloudy,
  WiFog,
  WiShowers,
  WiRain,
  WiThunderstorm,
  WiSleet,
  WiSnow,
} from 'weather-icons-react';import './Dashboard.css'; // Din befintliga CSS

function isToday(isoString) {
  const d = new Date(isoString);
  const today = new Date();
  return (
    d.getFullYear()  === today.getFullYear() &&
    d.getMonth()     === today.getMonth() &&
    d.getDate()      === today.getDate()
  );
}

// Väder-emoji
function getWeatherIcon(symbol) {
  const iconProps = { size: 48, color: "#333" };

  switch (symbol) {
    case 1:  return <WiDaySunny {...iconProps} />;           // Clear sky
    case 2:  return <WiDaySunnyOvercast {...iconProps} />;   // Nearly clear
    case 3:  return <WiDayCloudy {...iconProps} />;          // Variable cloudiness
    case 4:  return <WiDayCloudy {...iconProps} />;          // Halfclear
    case 5:  return <WiCloud {...iconProps} />;              // Cloudy
    case 6:  return <WiCloudy {...iconProps} />;             // Overcast
    case 7:  return <WiFog {...iconProps} />;                // Fog
    case 8:  return <WiShowers {...iconProps} />;            // Light rain showers
    case 9:  return <WiRain {...iconProps} />;               // Moderate rain showers
    case 10: return <WiRain {...iconProps} />;               // Heavy rain showers
    case 11: return <WiThunderstorm {...iconProps} />;       // Thunderstorm
    case 12: return <WiSleet {...iconProps} />;              // Light sleet showers
    case 13: return <WiSleet {...iconProps} />;              // Moderate sleet showers
    case 14: return <WiSleet {...iconProps} />;              // Heavy sleet showers
    case 15: return <WiSnow {...iconProps} />;               // Light snow showers
    case 16: return <WiSnow {...iconProps} />;               // Moderate snow showers
    case 17: return <WiSnow {...iconProps} />;               // Heavy snow showers
    case 18: return <WiRain {...iconProps} />;               // Light rain
    case 19: return <WiRain {...iconProps} />;               // Moderate rain
    case 20: return <WiRain {...iconProps} />;               // Heavy rain
    case 21: return <WiThunderstorm {...iconProps} />;       // Thunder
    case 22: return <WiSleet {...iconProps} />;              // Light sleet
    case 23: return <WiSleet {...iconProps} />;              // Moderate sleet
    case 24: return <WiSleet {...iconProps} />;              // Heavy sleet
    case 25: return <WiSnow {...iconProps} />;               // Light snowfall
    case 26: return <WiSnow {...iconProps} />;               // Moderate snowfall
    case 27: return <WiSnow {...iconProps} />;               // Heavy snowfall
    default: return <span style={{ fontSize: "2rem" }}>❓</span>;
  }
}

const Dashboard = () => {
  // Väder
  const [weather, setWeather]         = useState(null);
  const [dateLabel, setDateLabel]     = useState('');

  // SL-avgångar
  const [trips, setTrips]             = useState([]);
  const [loadingTrips, setLoadingTrips]   = useState(true);

  // Kalenderhändelser
  const [events, setEvents]           = useState([]);
  const [loadingEvents, setLoadingEvents] = useState(true);

  // Restid
  const [travelTime, setTravelTime]       = useState(null);
  const [loadingTravel, setLoadingTravel] = useState(true);

  // Sätt dagens datumstext
  useEffect(() => {
    const today = new Date();
    setDateLabel(
      today.toLocaleDateString('sv-SE', {
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
      })
    );
  }, []);

  // Hämta väder
  useEffect(() => {
    fetch('/api/weather')
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(setWeather)
      .catch(() => setWeather(null));
  }, []);

  // Hämta SL-avgångar
  useEffect(() => {
    fetch('/api/getSlSiteTrips?siteId=7407', { credentials: 'include' })
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(data => setTrips(data.departures || []))
      .catch(() => setTrips([]))
      .finally(() => setLoadingTrips(false));
  }, []);

  // Hämta kalenderhändelser
  useEffect(() => {
    fetch('/api/calendar/events', {
      credentials: 'include'
    })
      .then(res => {
        if (res.status === 401) {
          // inte inloggad → omdirigera hela sidan till Google OAuth2
          window.location.href = 'http://localhost:8080/oauth2/authorization/google';
          // returnera en tom Promise så vi inte går vidare i kedjan
          return new Promise(() => {});
        }
        if (!res.ok) {
          // andra serverfel
          return Promise.reject(new Error(`HTTP ${res.status}`));
        }
        return res.json();
      })
      .then(data => {
        if (data) {
          setEvents(data);
        }
      })
      .catch(err => {
        console.error('Fel vid hämtning av kalenderhändelser:', err);
        setEvents([]);
      })
      .finally(() => {
        setLoadingEvents(false);
      });
  }, []);

  // Hämta restid med trafik
  useEffect(() => {
    setLoadingTravel(true);
    // Exempel: Stockholm → Uppsala
    const origin      = encodeURIComponent('Tumba,Sweden');
    const destination = encodeURIComponent('Norrköping,Sweden');

    fetch(`/api/travel-time?origin=${origin}&destination=${destination}`, {
      credentials: 'include'
    })
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(data => setTravelTime(data))
      .catch(() => setTravelTime(null))
      .finally(() => setLoadingTravel(false));
  }, []);

  // Filtrera endast dagens avgångar & events
  const todayTrips  = useMemo(() => trips.filter(t => isToday(t.expected)), [trips]);
  const todayEvents = useMemo(() => events.filter(e =>
    isToday(e.start.dateTime || e.start.date)
  ), [events]);

  return (
    <div className="dashboard-grid">
      {/* Kort 1: Dagens datum + väder */}
      <div className="dashboard-card">
        <p style={{fontSize: '2 rem'}}>{dateLabel}</p>
        {weather ? (
          <>
            <p style={{ fontSize: '2rem' }}>{getWeatherIcon(weather.symbol)}</p>
            <p style={{ fontSize: '1.2rem' }}>{weather.temperature}°C</p>
          </>
        ) : (
          <p>Laddar väder…</p>
        )}
      </div>
      

      {/* Kort 2: SL-avgångar idag */}
      <div className="dashboard-card trips">
        <h2>Avgångar idag</h2>
        {loadingTrips ? (
          <p>Laddar…</p>
        ) : todayTrips.length ? (
          <ul className="trip-list">
            {todayTrips.map((d, i) => (
              <li key={i}>
                <span className="trip-time">{d.display}</span>
                <span className="trip-line">Linje {d.line.designation}</span>
                <span className="trip-dest">{d.destination}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p>Inga avgångar kvar idag.</p>
        )}
      </div>

      {/* Kort 3: Kalenderhändelser idag */}
      <div className="dashboard-card events">
        <h2>Händelser idag</h2>
        {loadingEvents ? (
          <p>Laddar…</p>
        ) : todayEvents.length ? (
          <ul className="event-list">
            {todayEvents.map((e, i) => {
              const start = e.start.dateTime || e.start.date;
              const time  = new Date(start).toLocaleTimeString('sv-SE', {
                hour: '2-digit', minute: '2-digit'
              });
              return (
                <li key={i}>
                  <span className="event-time">{time}</span>
                  <span className="event-summary">{e.summary}</span>
                </li>
              );
            })}
          </ul>
        ) : (
          <p>Inga händelser kvar idag.</p>
        )}
      </div>

      {/* Kort 4: Restid med trafik */}
      <div className="dashboard-card travel">
        <h2>Restid</h2>
        {loadingTravel ? (
          <p>Laddar restid…</p>
        ) : travelTime ? (
          <div className="travel-info">            
            <p><strong>Från:</strong> {travelTime.origin}</p>
            <p><strong>Till:</strong> {travelTime.destination}</p>
            <p><strong>Tid:</strong> {travelTime.durationInTraffic}</p>
          </div>
        ) : (
          <p>Kunde inte hämta restid.</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
