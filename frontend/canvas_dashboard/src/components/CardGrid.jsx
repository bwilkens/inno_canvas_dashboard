import { data } from "../api/tempHardCodedDataCourses.js";
import "../css/card-grid.css";

//TODO fetch actual data based on logged in user
const CardGrid = () => {
  const semesterStartDate = new Date("2025-09-01");
  const semesterEndDate = new Date("2025-12-31");

  const activeCards = data.filter(
    (item) =>
      new Date(item.date) >= semesterStartDate &&
      new Date(item.date) <= semesterEndDate
  );
  const nonActiveCards = data.filter(
    (item) =>
      new Date(item.date) < semesterStartDate ||
      new Date(item.date) > semesterEndDate
  );

  return (
    <div>
      {/* Active cards */}
      <div className="grid-container">
        {activeCards.map((item, index) => (
          <a href="">
            <div key={index} className="card">
              <h3>{item.title}</h3>
              <p>Start datum: {item.date}</p>
              <p>Laatste update: {item.last_update}</p>
              <p>Cursus Code: {item.course_code}</p>
            </div>
          </a>
        ))}
      </div>

      {nonActiveCards.length > 0 && <hr className="separator-line" />}

      {/* Non active cards */}
      <div className="grid-container">
        {nonActiveCards.map((item, index) => (
          <a href="">
            <div key={index} className="card outdated">
              <h3>{item.title}</h3>
              <p>Start datum: {item.date}</p>
              <p>Laatste update: {item.last_update}</p>
              <p>Cursus Code: {item.course_code}</p>
            </div>
          </a>
        ))}
      </div>
    </div>
  );
};

export default CardGrid;
