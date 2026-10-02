'use strict';
const { ApiError } = require('../utils/apiResponse');

/**
 * RBAC Role Enforcement Middleware Factory
 *
 * Creates a middleware that permits only users whose `dbUser.role`
 * is in the provided allowedRoles list.
 *
 * Usage:
 *   const role = require('../middlewares/role');
 *   router.delete('/vendor/:id', auth, role('admin'), controller);
 *   router.post('/audit',        auth, role('phi', 'admin'), controller);
 *
 * Roles: 'customer' | 'vendor' | 'phi' | 'admin'
 */
const roleMiddleware = (...allowedRoles) => (req, res, next) => {
  if (!req.user?.dbUser) {
    return next(new ApiError(401, 'Authentication required before role check'));
  }

  const { role } = req.user.dbUser;

  if (!allowedRoles.includes(role)) {
    return next(
      new ApiError(
        403,
        `Access denied. Required role(s): [${allowedRoles.join(', ')}]. Your role: ${role}`,
      ),
    );
  }

  next();
};

module.exports = roleMiddleware;
