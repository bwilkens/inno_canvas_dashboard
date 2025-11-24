// Base url of the container TODO change
const BASE_URL = import.meta.env.VITE_DASHBOARD_BASE_URL || "http://localhost:5000";

export const getDashboardUrl = (instanceName, role, email) => {
  console.log(instanceName, role);
  if (role === "ADMIN" || role === "TEACHER") {
    return `${BASE_URL}/${instanceName}/dashboard_${instanceName}/index.html`;
  } else if (role === "STUDENT") {
    if(!email) {
      return `${BASE_URL}/404.html`;
    }
    const firstPartEmail = email.split("@")[0];
    return `${BASE_URL}/${instanceName}/dashboard_${instanceName}/${instanceName}/students/${firstPartEmail}_index.html`;
  } else {
    return `${BASE_URL}/404.html`;
  }
};
