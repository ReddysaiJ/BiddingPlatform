import { Routes } from '@angular/router';

import { AuthGuard } from './core/auth/auth.guard';

import { RoleGuard } from './core/auth/role.guard';

import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

import { AuctionListComponent } from './features/auction/auction-list.component';

import { AuctionDetailComponent } from './features/auction/auction-detail.component';

import { MyBidsComponent } from './features/bid/my-bids.component';

import { AuctionCreateComponent } from './features/auction/auctionCreate.component';

import { MyAuctionsComponent } from './features/auction/my-auction.component';

import { LoginComponent } from './features/auth/login.component';

export const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent,
    },

    {
        path: '',
        component: MainLayoutComponent,
        canActivate: [AuthGuard],

        children: [
            {
                path: '',
                redirectTo: 'auctions',
                pathMatch: 'full',
            },

            {
                path: 'auctions',
                component: AuctionListComponent,
            },

            {
                path: 'auctions/:id',
                component: AuctionDetailComponent,
            },

            {
                path: 'auction/create',
                component: AuctionCreateComponent,
                canActivate: [RoleGuard],
                data: {
                    role: 'ROLE_SELLER',
                },
            },

            {
                path: 'my-auctions',
                component: MyAuctionsComponent,
                canActivate: [RoleGuard],
                data: {
                    role: 'ROLE_SELLER',
                },
            },

            {
                path: 'my-bids',
                component: MyBidsComponent,
            },
        ],
    },

    {
        path: '**',
        redirectTo: '',
    },
];
