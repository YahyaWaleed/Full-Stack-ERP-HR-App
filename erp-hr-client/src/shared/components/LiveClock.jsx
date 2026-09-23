import { useState, useEffect } from 'react';

function LiveClock() {
  const [now, setNow] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(timer); // cleanup when the component unmounts
  }, []);

  const dateStr = now.toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  const timeStr = now.toLocaleTimeString();

  return (
    <div className="live-clock">
      <span>{dateStr}</span>
      <span className="clock-time">{timeStr}</span>
    </div>
  );
}

export default LiveClock;