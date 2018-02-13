(ns clicky-goo.credentials
  (:import [com.google.api.ads.common.lib.auth OfflineCredentials$Api OfflineCredentials$Builder]
           com.google.api.ads.adwords.lib.client.AdWordsSession$Builder
           com.google.api.client.auth.oauth2.Credential))

(defn- adwords-creds-builder []
  (-> (OfflineCredentials$Builder.)
      (.forApi OfflineCredentials$Api/ADWORDS)))

(defn offline-credentials
  [config-file & refresh-token]
  (as-> (adwords-creds-builder) builder
    (.fromFile builder config-file)
    (if refresh-token
      (.withRefreshToken refresh-token)
      builder)
    (.generateCredential (.build builder))))

(defn adwords-session [config-file ^Credential cred
                       & {:keys [client-customer-id user-agent enable-partial-failure?]}]
  (let [asb (AdWordsSession$Builder.)]
    (.fromFile asb config-file)
    (.withOAuth2Credential asb cred)
    (when client-customer-id
      (.withClientCustomerId asb client-customer-id))
    (when user-agent
      (.withUserAgent asb user-agent))
    (when enable-partial-failure?
      (.enablePartialFailure asb))
    (.build asb)))
