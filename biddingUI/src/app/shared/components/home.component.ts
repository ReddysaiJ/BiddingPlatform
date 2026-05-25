import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule],
    template: `
        <!-- Hero Section -->
        <section class="hero-section">
            <div class="container">
                <div class="row align-items-center min-vh-100">
                    <div class="col-lg-6">
                        <span class="hero-badge">
                            <i class="bi bi-lightning-charge-fill"></i>
                            Real-Time Auction Platform
                        </span>

                        <h1 class="hero-title mt-4">
                            Bid Smart.
                            <span class="gradient-text">Win Faster.</span>
                        </h1>

                        <p class="hero-description mt-4">
                            Experience real-time online auctions with secure bidding,
                            live updates, seller dashboards, and instant winner tracking.
                        </p>

                        <div class="d-flex flex-wrap gap-3 mt-4">
                            <button
                                class="btn btn-primary btn-lg px-4"
                                (click)="goToAuctions()"
                            >
                                <i class="bi bi-hammer me-2"></i>
                                Explore Auctions
                            </button>

                            <button
                                class="btn btn-outline-light btn-lg px-4"
                                (click)="login()"
                                *ngIf="!isLoggedIn()"
                            >
                                <i class="bi bi-box-arrow-in-right me-2"></i>
                                Login
                            </button>
                        </div>

                        <div class="hero-stats mt-5">
                            <div class="stat-box">
                                <h3>100+</h3>
                                <p>Active Auctions</p>
                            </div>

                            <div class="stat-box">
                                <h3>24/7</h3>
                                <p>Live Bidding</p>
                            </div>

                            <div class="stat-box">
                                <h3>Secure</h3>
                                <p>OAuth2 Security</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-6 text-center mt-5 mt-lg-0">
                        <div class="hero-card">
                            <div class="auction-preview">
                                <div class="auction-image">
                                    <i class="bi bi-house-door-fill"></i>
                                </div>

                                <div class="auction-content">
                                    <h5>Luxury Duplex House</h5>

                                    <div class="d-flex justify-content-between mt-3">
                                        <span class="text-muted">Current Bid</span>
                                        <span class="fw-bold text-success">
                                            ₹20,00,002
                                        </span>
                                    </div>

                                    <div class="progress mt-3">
                                        <div
                                            class="progress-bar"
                                            style="width: 75%"
                                        ></div>
                                    </div>

                                    <button class="btn btn-dark w-100 mt-4">
                                        Live Auction Running
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Features -->
        <section class="features-section py-5">
            <div class="container">
                <div class="text-center mb-5">
                    <h2 class="section-title">
                        Why Choose AuctionRealm?
                    </h2>

                    <p class="section-subtitle">
                        Modern auction experience powered by Spring Boot,
                        Angular, Kafka and WebSockets.
                    </p>
                </div>

                <div class="row g-4">
                    <div class="col-md-4">
                        <div class="feature-card h-100">
                            <div class="feature-icon">
                                <i class="bi bi-broadcast"></i>
                            </div>

                            <h4>Real-Time Updates</h4>

                            <p>
                                Receive instant bid updates using WebSocket
                                powered live streaming.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="feature-card h-100">
                            <div class="feature-icon">
                                <i class="bi bi-shield-check"></i>
                            </div>

                            <h4>Secure Authentication</h4>

                            <p>
                                Enterprise-grade OAuth2 authentication with
                                Keycloak and PKCE support.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="feature-card h-100">
                            <div class="feature-icon">
                                <i class="bi bi-graph-up-arrow"></i>
                            </div>

                            <h4>Seller Dashboard</h4>

                            <p>
                                Manage auctions, track bids, and declare winners
                                with ease.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- How It Works -->
        <section class="workflow-section py-5">
            <div class="container">
                <div class="text-center mb-5">
                    <h2 class="section-title">
                        How It Works
                    </h2>
                </div>

                <div class="row g-4">
                    <div class="col-md-3">
                        <div class="workflow-card">
                            <div class="workflow-number">1</div>
                            <h5>Create Account</h5>
                            <p>Login securely using Keycloak authentication.</p>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="workflow-card">
                            <div class="workflow-number">2</div>
                            <h5>Browse Auctions</h5>
                            <p>Explore active auctions across categories.</p>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="workflow-card">
                            <div class="workflow-number">3</div>
                            <h5>Place Live Bids</h5>
                            <p>Bid in real-time and compete instantly.</p>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="workflow-card">
                            <div class="workflow-number">4</div>
                            <h5>Win Auctions</h5>
                            <p>Track winners and manage purchases easily.</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- CTA -->
        <section class="cta-section py-5">
            <div class="container text-center">
                <div class="cta-card">
                    <h2 class="fw-bold">
                        Ready to Start Bidding?
                    </h2>

                    <p class="text-muted mt-3">
                        Join the real-time auction experience today.
                    </p>

                    <button
                        class="btn btn-primary btn-lg mt-3 px-5"
                        (click)="login()"
                    >
                        Get Started
                    </button>
                </div>
            </div>
        </section>
    `,
    styles: [`
        .hero-section {
            background:
                linear-gradient(
                    135deg,
                    #0f172a 0%,
                    #1e293b 40%,
                    #2563eb 100%
                );
            color: white;
            overflow: hidden;
        }

        .hero-badge {
            background: rgba(255,255,255,0.12);
            border: 1px solid rgba(255,255,255,0.2);
            padding: 10px 18px;
            border-radius: 999px;
            font-size: 0.9rem;
            backdrop-filter: blur(10px);
        }

        .hero-title {
            font-size: 4rem;
            font-weight: 800;
            line-height: 1.1;
        }

        .gradient-text {
            background: linear-gradient(90deg, #60a5fa, #c084fc);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero-description {
            color: rgba(255,255,255,0.75);
            font-size: 1.1rem;
            max-width: 550px;
        }

        .hero-card {
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 24px;
            padding: 24px;
            backdrop-filter: blur(14px);
        }

        .auction-preview {
            background: white;
            color: black;
            border-radius: 20px;
            overflow: hidden;
        }

        .auction-image {
            height: 220px;
            background: linear-gradient(135deg,#667eea,#764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 5rem;
        }

        .auction-content {
            padding: 24px;
        }

        .hero-stats {
            display: flex;
            gap: 18px;
            flex-wrap: wrap;
        }

        .stat-box {
            background: rgba(255,255,255,0.08);
            padding: 18px 24px;
            border-radius: 16px;
            min-width: 140px;
        }

        .stat-box h3 {
            margin: 0;
            font-weight: 700;
        }

        .stat-box p {
            margin: 0;
            color: rgba(255,255,255,0.7);
            font-size: 0.9rem;
        }

        .section-title {
            font-size: 2.5rem;
            font-weight: 700;
        }

        .section-subtitle {
            color: #6b7280;
            max-width: 700px;
            margin: auto;
        }

        .feature-card {
            background: white;
            border-radius: 20px;
            padding: 32px;
            border: 1px solid #e5e7eb;
            transition: 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.08);
        }

        .feature-icon {
            width: 70px;
            height: 70px;
            border-radius: 18px;
            background: #eff6ff;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
            font-size: 1.8rem;
            color: #2563eb;
        }

        .workflow-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            text-align: center;
            border: 1px solid #e5e7eb;
            height: 100%;
        }

        .workflow-number {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: linear-gradient(135deg,#2563eb,#7c3aed);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.4rem;
            font-weight: bold;
            margin: auto auto 20px;
        }

        .cta-card {
            background: white;
            padding: 60px 30px;
            border-radius: 24px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 20px 50px rgba(0,0,0,0.05);
        }

        @media(max-width: 768px) {
            .hero-title {
                font-size: 2.6rem;
            }

            .hero-stats {
                flex-direction: column;
            }
        }
    `]
})
export class HomeComponent {

    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    login(): void {
        this.authService.login();
    }

    goToAuctions(): void {
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/auctions']);
        } else {
            this.login();
        }
    }

    isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }
}
