import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  Sprout,
  ShoppingBag,
  Layers,
  Boxes,
  ShoppingCart,
  CreditCard,
  GraduationCap,
  Award,
  Truck,
  Car,
  Building2,
  MessageSquare,
  BarChart3,
  X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const navSections = [
  {
    title: 'Core',
    items: [
      { name: 'Dashboard', path: '/', icon: LayoutDashboard, roles: ['ADMIN', 'FINANCE_MANAGER', 'INVENTORY_MANAGER', 'DISPATCH_MANAGER', 'SERVICE_MANAGER', 'TRAINER'] },
      { name: 'Analytics & Reports', path: '/reports', icon: BarChart3, roles: ['ADMIN', 'FINANCE_MANAGER', 'INVENTORY_MANAGER', 'DISPATCH_MANAGER', 'TRAINER'] },
    ],
  },
  {
    title: 'E-Commerce & Logistics',
    items: [
      { name: 'Orders', path: '/orders', icon: ShoppingCart, roles: ['ADMIN', 'DISPATCH_MANAGER', 'INVENTORY_MANAGER', 'FINANCE_MANAGER'] },
      { name: 'Products Catalog', path: '/products', icon: ShoppingBag, roles: ['ADMIN', 'INVENTORY_MANAGER'] },
      { name: 'Categories', path: '/categories', icon: Layers, roles: ['ADMIN', 'INVENTORY_MANAGER'] },
      { name: 'Inventory & Stock', path: '/inventory', icon: Boxes, roles: ['ADMIN', 'INVENTORY_MANAGER', 'FINANCE_MANAGER'] },
      { name: 'Payments & Revenue', path: '/payments', icon: CreditCard, roles: ['ADMIN', 'FINANCE_MANAGER'] },
      { name: 'Dispatch & Deliveries', path: '/dispatch', icon: Truck, roles: ['ADMIN', 'DISPATCH_MANAGER', 'DRIVER'] },
      { name: 'Fleet & Drivers', path: '/drivers', icon: Car, roles: ['ADMIN', 'DISPATCH_MANAGER'] },
    ],
  },
  {
    title: 'Outgrowers & Training',
    items: [
      { name: 'Training Sessions', path: '/trainings', icon: GraduationCap, roles: ['ADMIN', 'TRAINER'] },
      { name: 'Certifications', path: '/certifications', icon: Award, roles: ['ADMIN', 'TRAINER'] },
      { name: 'Farmers Directory', path: '/farmers', icon: Sprout, roles: ['ADMIN', 'TRAINER'] },
    ],
  },
  {
    title: 'Directory & Feedback',
    items: [
      { name: 'Users & Roles', path: '/users', icon: Users, roles: ['ADMIN'] },
      { name: 'Customers', path: '/customers', icon: UserCheck, roles: ['ADMIN', 'SERVICE_MANAGER'] },
      { name: 'Suppliers', path: '/suppliers', icon: Building2, roles: ['ADMIN', 'INVENTORY_MANAGER'] },
      { name: 'Feedback & Inquiries', path: '/feedback', icon: MessageSquare, roles: ['ADMIN', 'SERVICE_MANAGER'] },
    ],
  },
];

const Sidebar = ({ isOpen, onClose }) => {
  const { user } = useAuth();

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-40 lg:hidden" onClick={onClose} />
      )}

      <aside
        className={`fixed top-0 left-0 bottom-0 w-64 bg-slate-900 text-slate-300 z-50 transition-transform duration-200 ease-in-out flex flex-col ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        {/* Brand Header */}
        <div className="h-16 flex items-center justify-between px-6 border-b border-slate-800 bg-slate-950/40">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-emerald-600 to-green-400 flex items-center justify-center text-white shadow-md shadow-emerald-900/30">
              <Sprout className="w-5 h-5" />
            </div>
            <div>
              <span className="font-extrabold text-white text-base tracking-tight leading-none block">AAA GROWERS</span>
              <span className="text-[10px] text-emerald-400 font-semibold uppercase tracking-wider block mt-0.5">Enterprise Portal</span>
            </div>
          </div>
          <button onClick={onClose} className="lg:hidden p-1.5 text-slate-400 hover:text-white rounded-lg">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation items */}
        <nav className="flex-1 overflow-y-auto px-4 py-4 space-y-6">
          {navSections.map((section, idx) => {
            const allowedItems = section.items.filter(
              (item) => user?.role === 'ADMIN' || item.roles.includes(user?.role)
            );
            if (allowedItems.length === 0) return null;

            return (
              <div key={idx}>
                <p className="px-3 text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2">
                  {section.title}
                </p>
                <div className="space-y-1">
                  {allowedItems.map((item) => (
                    <NavLink
                      key={item.path}
                      to={item.path}
                      onClick={() => {
                        if (window.innerWidth < 1024) onClose();
                      }}
                      className={({ isActive }) =>
                        `flex items-center gap-3 px-3 py-2 rounded-xl text-sm font-medium transition-all ${
                          isActive
                            ? 'bg-emerald-600 text-white shadow-sm shadow-emerald-950/40 font-semibold'
                            : 'text-slate-300 hover:bg-slate-800/80 hover:text-white'
                        }`
                      }
                    >
                      <item.icon className="w-4 h-4 opacity-80" />
                      <span>{item.name}</span>
                    </NavLink>
                  ))}
                </div>
              </div>
            );
          })}
        </nav>

        {/* Footer info */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/30 text-xs text-slate-400 flex items-center justify-between">
          <span>v1.0.0 Enterprise</span>
          <span className="inline-block w-2 h-2 rounded-full bg-emerald-500"></span>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
