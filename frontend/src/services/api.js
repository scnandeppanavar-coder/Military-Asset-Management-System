import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Attach JWT token to requests if stored in localStorage
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('mams_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercept responses for auth expiration handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('mams_token');
      localStorage.removeItem('mams_user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// ==========================================
// Authentication APIs
// ==========================================
export const login = async (credentials) => {
  const response = await api.post('/auth/login', credentials);
  return response.data;
};

export const register = async (userData) => {
  const response = await api.post('/auth/register', userData);
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await api.get('/auth/me');
  return response.data;
};

// ==========================================
// Dashboard APIs
// ==========================================
export const getDashboard = async (params = {}) => {
  return await api.get('/dashboard', { params });
};

export const getNetMovement = async (params = {}) => {
  return await api.get('/dashboard/net-movement', { params });
};

// ==========================================
// Purchases APIs
// ==========================================
export const getPurchases = async (params = {}) => {
  return await api.get('/purchases', { params });
};

export const getPurchaseById = async (id) => {
  return await api.get(`/purchases/${id}`);
};

export const createPurchase = async (purchaseData) => {
  return await api.post('/purchases', purchaseData);
};

export const updatePurchase = async (id, purchaseData) => {
  return await api.put(`/purchases/${id}`, purchaseData);
};

export const deletePurchase = async (id) => {
  return await api.delete(`/purchases/${id}`);
};

// ==========================================
// Transfers APIs
// ==========================================
export const getTransfers = async (params = {}) => {
  return await api.get('/transfers', { params });
};

export const getTransferById = async (id) => {
  return await api.get(`/transfers/${id}`);
};

export const createTransfer = async (transferData) => {
  return await api.post('/transfers', transferData);
};

// ==========================================
// Assignments APIs
// ==========================================
export const getAssignments = async (params = {}) => {
  return await api.get('/assignments', { params });
};

export const getAssignmentById = async (id) => {
  return await api.get(`/assignments/${id}`);
};

export const createAssignment = async (assignmentData) => {
  return await api.post('/assignments', assignmentData);
};

export const returnAssignment = async (id, returnData) => {
  return await api.put(`/assignments/${id}`, returnData);
};

// ==========================================
// Expenditures APIs
// ==========================================
export const getExpenditures = async (params = {}) => {
  return await api.get('/expenditures', { params });
};

export const getExpenditureById = async (id) => {
  return await api.get(`/expenditures/${id}`);
};

export const createExpenditure = async (expenditureData) => {
  return await api.post('/expenditures', expenditureData);
};

// ==========================================
// Bases APIs
// ==========================================
export const getBases = async () => {
  return await api.get('/bases');
};

export const getBaseById = async (id) => {
  return await api.get(`/bases/${id}`);
};

export const createBase = async (baseData) => {
  return await api.post('/bases', baseData);
};

export const updateBase = async (id, baseData) => {
  return await api.put(`/bases/${id}`, baseData);
};

export const deleteBase = async (id) => {
  return await api.delete(`/bases/${id}`);
};

// ==========================================
// Equipment Types APIs
// ==========================================
export const getEquipmentTypes = async () => {
  return await api.get('/equipment-types');
};

export const getEquipmentTypeById = async (id) => {
  return await api.get(`/equipment-types/${id}`);
};

export const createEquipmentType = async (equipmentData) => {
  return await api.post('/equipment-types', equipmentData);
};

export const updateEquipmentType = async (id, equipmentData) => {
  return await api.put(`/equipment-types/${id}`, equipmentData);
};

export const deleteEquipmentType = async (id) => {
  return await api.delete(`/equipment-types/${id}`);
};

// ==========================================
// Users APIs (Admin Only)
// ==========================================
export const getUsers = async () => {
  return await api.get('/users');
};

export const getUserById = async (id) => {
  return await api.get(`/users/${id}`);
};

export const createUser = async (userData) => {
  return await api.post('/users', userData);
};

export const updateUser = async (id, userData) => {
  return await api.put(`/users/${id}`, userData);
};

export const deleteUser = async (id) => {
  return await api.delete(`/users/${id}`);
};

// ==========================================
// Audit Logs APIs (Admin Only)
// ==========================================
export const getAuditLogs = async (params = {}) => {
  return await api.get('/audit-logs', { params });
};

// Bundle methods onto api object for convenience
api.login = login;
api.register = register;
api.getCurrentUser = getCurrentUser;
api.getDashboard = getDashboard;
api.getNetMovement = getNetMovement;
api.getPurchases = getPurchases;
api.getPurchaseById = getPurchaseById;
api.createPurchase = createPurchase;
api.updatePurchase = updatePurchase;
api.deletePurchase = deletePurchase;
api.getTransfers = getTransfers;
api.getTransferById = getTransferById;
api.createTransfer = createTransfer;
api.getAssignments = getAssignments;
api.getAssignmentById = getAssignmentById;
api.createAssignment = createAssignment;
api.returnAssignment = returnAssignment;
api.getExpenditures = getExpenditures;
api.getExpenditureById = getExpenditureById;
api.createExpenditure = createExpenditure;
api.getBases = getBases;
api.getBaseById = getBaseById;
api.createBase = createBase;
api.updateBase = updateBase;
api.deleteBase = deleteBase;
api.getEquipmentTypes = getEquipmentTypes;
api.getEquipmentTypeById = getEquipmentTypeById;
api.createEquipmentType = createEquipmentType;
api.updateEquipmentType = updateEquipmentType;
api.deleteEquipmentType = deleteEquipmentType;
api.getUsers = getUsers;
api.getUserById = getUserById;
api.createUser = createUser;
api.updateUser = updateUser;
api.deleteUser = deleteUser;
api.getAuditLogs = getAuditLogs;

export { api };
export default api;
