import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';
import { AuctionListComponent } from './features/auction/auction-list.component';

export const routes: Routes = [
    {path: '', component: MainLayoutComponent, children: [
        // {path: '', component: HomeComponent},
        {path: 'auctions', component: AuctionListComponent},
        // {path: 'auction/:id', component: AuctionDetailComponent},
        // {path: 'auction/create', component: AuctionCreateComponent,
        //     canActivate: [AuthGuard, RoleGuard], data: {role: 'ROLE_SELLER'}},
        // {path: 'my-auctions', component: MyAuctionsComponent,
        //     canActivate: [AuthGuard, RoleGuard], data: {role: 'ROLE_SELLER'}},
        // {path: 'my-bids', component: MyBidsComponent,
        //     canActivate: [AuthGuard]}
    ]},
    {path: '**', redirectTo: ''}
];
