import React, { useState, useEffect } from "react";
import { getUserData } from "../api/getUserData.js";
import CardGridSkeleton from "./CardGridSkeleton";
import "../css/card-grid.css";

const CardGrid = () => {
  const [loading, setLoading] = useState(true);
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await getUserData();
        setCourses(data.courses);
      } catch (err) {
        console.error("Error loading courses:", err);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  if (loading) return <CardGridSkeleton />;

  const currentDate = new Date();

  const activeCards = courses.filter((item) => {
    const start = new Date(item.startDate);
    const end = new Date(item.endDate);
    return currentDate >= start && currentDate <= end;
  });

  const nonActiveCards = courses.filter((item) => {
    const start = new Date(item.startDate);
    const end = new Date(item.endDate);
    return currentDate < start || currentDate > end;
  });

  return (
    <div>
      {/* Active cards */}
      <div className="grid-container">
        {activeCards.map((item, index) => (
          <a href="#" key={index} className="card-link">
            <div className="card">
              <h3>{item.courseName}</h3>
              <p>Start datum: {item.startDate}</p>
              <p>Eind datum: {item.endDate}</p>
              <p>Cursus code: {item.instanceName}</p>
            </div>
          </a>
        ))}
      </div>

      {nonActiveCards.length > 0 && <hr className="separator-line" />}

      {/* Non-active cards */}
      <div className="grid-container">
        {nonActiveCards.map((item, index) => (
          <a href="#" key={index} className="card-link">
            <div className="card outdated">
              <h3>{item.courseName}</h3>
              <p>Start datum: {item.startDate}</p>
              <p>Eind datum: {item.endDate}</p>
              <p>Cursus Code: {item.instanceName}</p>
            </div>
          </a>
        ))}
      </div>
    </div>
  );
};

export default CardGrid;
