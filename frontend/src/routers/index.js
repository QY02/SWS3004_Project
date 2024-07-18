import {createRouter, createWebHistory} from 'vue-router';

const routes = [
    {
        path: '/',
        component: () => import('@/layouts/MainLayout.vue'),
        children: [
            {
                name: 'OuterHome',
                path: '/',
                component: () => import('@/components/OuterHome.vue')
            },
            {
                name: 'login',
                path: '/login',
                component: () => import('@/components/login.vue')
            },
            {
                path: '/register',
                component: () => import('@/components/register.vue')
            },
            {
                name: 'book',
                path: '/book',
                component: () => import('@/components/event/book/Steps.vue')
            },
            {
                name: 'publishEvent',
                path: '/publishEvent',
                component: () => import('@/components/event/PublishEvent.vue')
            },
            {
                name: 'home',
                path: '/home',
                component: () => import('@/components/home/HomePage.vue')
            },
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
