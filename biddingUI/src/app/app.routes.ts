import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';
import { MainLayoutComponent } from './layouts/main-layout.component';
import { AuctionListComponent } from './features/auction/auction-list.component';
import { AuctionDetailComponent } from './features/auction/auction-detail.component';
import { MyBidsComponent } from './features/bid/my-bids.component';
import { AuctionCreateComponent } from './features/auction/auctionCreate.component';
import { MyAuctionsComponent } from './features/auction/my-auction.component';
import { LoginComponent } from './features/auth/login.component';
import { CallbackComponent } from './features/callback/callback.component';
import { ProfileComponent } from './features/user/profile.component';
import { HomeComponent } from './shared/components/home.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'callback', component: CallbackComponent },
    {
        path: '', component: MainLayoutComponent,
        children: [
            { path: '', component: HomeComponent },
            { path: 'auctions', component: AuctionListComponent, canActivate: [AuthGuard] },
            { path: 'auctions/:id', component: AuctionDetailComponent, canActivate: [AuthGuard] },
            { path: 'auction/create', component: AuctionCreateComponent,
              canActivate: [RoleGuard, AuthGuard], data: { role: 'ROLE_SELLER' } },
            { path: 'my-auctions', component: MyAuctionsComponent,
              canActivate: [RoleGuard, AuthGuard], data: { role: 'ROLE_SELLER' } },
            { path: 'my-bids', component: MyBidsComponent, canActivate: [AuthGuard] },
            { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
        ],
    },
    { path: '**', redirectTo: '' },
];
