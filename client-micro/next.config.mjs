/** @type {import('next').NextConfig} */
const nextConfig = {
    output: "standalone",
    images: {
        remotePatterns: [
            {
                hostname: "res.cloudinary.com",
            },
        ],
    },
    reactStrictMode: false
};

export default nextConfig;
