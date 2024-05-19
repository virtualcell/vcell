import {Injectable} from '@angular/core';
import {BaseuriConfig} from './baseuri-config';

@Injectable({
    providedIn: 'root'
})
export class BaseuriConfigService {
    private configData: BaseuriConfig | undefined;

    async loadConfiguration(): Promise<BaseuriConfig> {
        try {
            return new Promise<BaseuriConfig>((resolve, reject) => {
                try {
                    const url = window.location.origin;
                    this.configData = {baseUri: url};
                    resolve(this.configData);
                } catch (err) {
                    reject(err);
                }
            });
        } catch (err) {
            return Promise.reject(err);
        }
    }

    get config(): BaseuriConfig | undefined {
        return this.configData;
    }
}
