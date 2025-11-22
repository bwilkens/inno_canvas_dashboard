export async function getCourses() {
  const response = await fetch("/api/v1/dashboard/users/", {
    method: "GET",
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error("Could not fetch courses");
  }

  return await response.json();
}
