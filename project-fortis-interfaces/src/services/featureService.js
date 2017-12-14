import request from 'request';
import { reactAppFeatureServiceHost } from '../config';

function fetchLocationsFromFeatureService(bbox, matchName, namespace, callback) {
  if (!matchName || !bbox || bbox.length !== 4) {
    return callback(null, []);
  }

  const url = `${reactAppFeatureServiceHost}/features/bbox/${bbox.join('/')}?include=bbox,centroid&filter_namespace=${namespace}&filter_name=${matchName}`;

  request({ url, json: true }, (err, response) =>
    callback(err, (response && response.body && response.body.features) || []));
}

export {
  fetchLocationsFromFeatureService,
};