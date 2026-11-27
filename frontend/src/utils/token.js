const KEY = "token";

export const saveToken = (token) => {
  try {
    localStorage.setItem(KEY, token);
    return true;
  } catch {
    return false;
  }
};

export const getToken = () => {
  try {
    return localStorage.getItem(KEY);
  } catch {
    return null;
  }
};

export const removeToken = () => {
  try {
    localStorage.removeItem(KEY);
    return true;
  } catch {
    return false;
  }
};
