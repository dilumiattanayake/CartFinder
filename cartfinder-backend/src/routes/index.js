'use strict';
const express = require('express');
const router = express.Router();

router.use('/auth',        require('./auth.routes'));
router.use('/vendors',     require('./vendor.routes'));
router.use('/menus',       require('./menu.routes'));
router.use('/reviews',     require('./review.routes'));
router.use('/complaints',  require('./complaint.routes'));
router.use('/inspections', require('./inspection.routes'));
router.use('/admin',       require('./admin.routes'));

module.exports = router;
