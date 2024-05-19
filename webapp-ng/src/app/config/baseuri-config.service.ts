import {Injectable} from '@angular/core';
import {BaseuriConfig} from './baseuri-config';
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class BaseuriConfigService {
    private configData: BaseuriConfig | undefined;
    private readonly configPath: string = '/assets/config/baseuri_config.json';

    constructor(
        private http: HttpClient
    ) { }

    async loadConfiguration(): Promise<BaseuriConfig> {
        try {
            this.configData = await this.http.get<BaseuriConfig>(this.configPath).toPromise();
            return this.configData;
        } catch (err) {
            return Promise.reject(err);
        }
    }

    get config(): BaseuriConfig | undefined {
        return this.configData;
    }
}
